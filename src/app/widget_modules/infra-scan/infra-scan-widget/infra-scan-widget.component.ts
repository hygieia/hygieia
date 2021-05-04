import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ComponentFactoryResolver,
  OnDestroy,
  OnInit,
  ViewChild
} from '@angular/core';
import {WidgetComponent} from '../../../shared/widget/widget.component';
import {DashboardService} from '../../../shared/dashboard.service';
import {OneChartLayoutComponent} from '../../../shared/layouts/one-chart-layout/one-chart-layout.component';
import {InfraScanService} from '../infra-scan.service';
import {INFRA_SCAN_CHARTS} from './infra-scan-charts';
import {catchError, distinctUntilChanged, map, startWith, switchMap, take} from 'rxjs/operators';
import {of, Subscription} from 'rxjs';
import {WidgetState} from '../../../shared/widget-header/widget-state';
import {LayoutDirective} from '../../../shared/layouts/layout.directive';
import {InfraScan} from '../infra-scan-interfaces';
import {DashStatus} from '../../../shared/dash-status/DashStatus';
import {
  IClickListData,
  IClickListItemInfra
} from '../../../shared/charts/click-list/click-list-interfaces';
import {InfraScanDetailComponent} from '../infra-scan-detail/infra-scan-detail.component';

@Component({
  selector: 'app-infra-scan-widget',
  templateUrl: './infra-scan-widget.component.html',
  styleUrls: ['./infra-scan-widget.component.sass']
})
export class InfraScanWidgetComponent extends WidgetComponent implements OnInit, AfterViewInit, OnDestroy {

  private intervalRefreshSubscription: Subscription;
  private params;

  @ViewChild(LayoutDirective, {static: true}) childLayoutTag: LayoutDirective;

  constructor(componentFactoryResolver: ComponentFactoryResolver,
              cdr: ChangeDetectorRef,
              dashboardService: DashboardService,
              private infraScanService: InfraScanService) {
    super(componentFactoryResolver, cdr, dashboardService);
  }

  ngOnInit() {
    this.widgetId = 'infrascan0';
    this.layout = OneChartLayoutComponent;
    this.charts = INFRA_SCAN_CHARTS;
    this.auditType = 'INFRASTRUCTURE_SCAN';
    this.init();
  }

  ngAfterViewInit(): void {
    this.startRefreshInterval();
  }

  ngOnDestroy(): void {
    this.stopRefreshInterval();
  }

  startRefreshInterval() {
    this.intervalRefreshSubscription = this.dashboardService.dashboardRefresh$.pipe(
      startWith(-1), // Refresh this widget separate from dashboard (ex. config is updated)
      distinctUntilChanged(), // If dashboard is loaded the first time, ignore widget double refresh
      // tslint:disable-next-line:no-shadowed-variable
      switchMap(_ => this.getCurrentWidgetConfig()),
      switchMap(widgetConfig => {
        if (!widgetConfig) {
          this.widgetConfigExists = false;
          return of([]);
        }
        this.widgetConfigExists = true;
        // check if collector item type is tied to dashboard
        // if true, set state to READY, otherwise keep at default CONFIGURE
        if (this.dashboardService.checkCollectorItemTypeExist('InfrastructureScan')) {
          this.state = WidgetState.READY;
        }
        return this.dashboardService.dashboardConfig$.pipe(take(1),
          map(dashboard => dashboard.application.components[0].collectorItems.InfrastructureScan),
          map(colItems => Object.assign(widgetConfig, { collectorItemId: colItems[0].id })));
      }),
      switchMap(uWidgetConfig => {
        this.params = { componentId: uWidgetConfig.componentId, collectorItemId: uWidgetConfig.collectorItemId, max: 1};
        return this.infraScanService.getInfraScanDetails(this.params).pipe(catchError(err => of(err)));
        })
    ).subscribe(result => {
      this.hasData = (result && result.length > 0);
      if (this.hasData) {
        this.loadCharts(result);
      } else {
        this.setDefaultIfNoData();
      }
      super.loadComponent(this.childLayoutTag);
    });
  }

  private stopRefreshInterval() {
    if (this.intervalRefreshSubscription) {
      this.intervalRefreshSubscription.unsubscribe();
    }
  }

  private loadCharts(result: InfraScan[]) {
    const sData = result[0].vulnerabilities.map(v => {
      const riskStatus = v.contextualizedRiskLabel === 'HIGH' ?
        DashStatus.CRITICAL : (v.contextualizedRiskLabel === 'MEDIUM' ? DashStatus.WARN : DashStatus.PASS);
      return {
        title: v.vulnerabilityTitle,
        subtitles : [v.contextualizedRiskScore],
        status: riskStatus,
        statusText: v.vulnerabilityStatus,
        vulnerability: v,
      } as IClickListItemInfra;
    });

    this.charts[0].data = {
      items: sData,
      clickableContent: InfraScanDetailComponent,
      clickableHeader: null
    } as IClickListData;
  }

  private setDefaultIfNoData() {
    if (!this.hasData) {
      this.charts[0].data = { items: [{ title: 'No Data Found' }]};
    }
    super.loadComponent(this.childLayoutTag);
  }
}
