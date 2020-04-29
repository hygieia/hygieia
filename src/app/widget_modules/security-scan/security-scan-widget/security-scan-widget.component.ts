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
import {ActivatedRoute} from '@angular/router';
import {SecurityScanService} from '../security-scan.service';
import {catchError, distinctUntilChanged, startWith, switchMap} from 'rxjs/operators';
import {LayoutDirective} from '../../../shared/layouts/layout.directive';
import {ISecurityScan} from '../security-scan-interfaces';
import {of, Subscription} from 'rxjs';
import {SECURITY_SCAN_CHARTS} from '../security-scan-widget/security-scan-charts';
import {
  IClickListData,
  IClickListItem,
} from '../../../shared/charts/click-list/click-list-interfaces';
import {OneChartLayoutComponent} from '../../../shared/layouts/one-chart-layout/one-chart-layout.component';
import {DashStatus} from '../../../shared/dash-status/DashStatus';

@Component({
  selector: 'app-security-scan-widget',
  templateUrl: './security-scan-widget.component.html',
  styleUrls: ['./security-scan-widget.component.sass']
})
export class SecurityScanWidgetComponent extends WidgetComponent implements OnInit, AfterViewInit, OnDestroy {

  private intervalRefreshSubscription: Subscription;
  private params;
  @ViewChild(LayoutDirective, {static: true}) childLayoutTag: LayoutDirective;

  constructor(componentFactoryResolver: ComponentFactoryResolver,
              cdr: ChangeDetectorRef,
              dashboardService: DashboardService,
              route: ActivatedRoute,
              private securityService: SecurityScanService) {
    super(componentFactoryResolver, cdr, dashboardService, route);
  }

  ngOnInit() {
    this.widgetId = 'codeanalysis0';
    this.layout = OneChartLayoutComponent;
    this.charts = SECURITY_SCAN_CHARTS;
    this.auditType = 'STATIC_SECURITY_ANALYSIS';
    this.init();
  }
  ngAfterViewInit() {
    this.startRefreshInterval();
  }

  ngOnDestroy() {
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
          return of([]);
        }
        this.params = {
          componentId: widgetConfig.componentId,
          max: 1
        };
        return this.securityService.getSecurityScanDetails(this.params.componentId, this.params.max)
          .pipe(catchError(err => of(err)));
        // );
      })).subscribe(result => {
      if (result && result.length > 0) {
        this.loadCharts(result);
      }
      super.loadComponent(this.childLayoutTag);
    });
  }

  stopRefreshInterval() {
    if (this.intervalRefreshSubscription) {
      this.intervalRefreshSubscription.unsubscribe();
    }
  }

  loadCharts(result: ISecurityScan[]) {
    this.generateSecurityScanData(result);
  }

  generateSecurityScanData(result: ISecurityScan[]) {
    const sData = result[0].metrics.map(metric => {
      const riskStatus = metric.name === 'High' ? DashStatus.CRITICAL : (metric.name === 'Medium' ?
        DashStatus.WARN : DashStatus.PASS);
      return {
        title: metric.name,
        subtitles : [metric.value],
        status: riskStatus,
        statusText: metric.status,
      } as IClickListItem;
    });

    this.charts[0].data = {
      items: sData,
      clickableContent: null,
      clickableHeader: null
    } as IClickListData;
  }
}
