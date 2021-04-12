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
import {SecurityScanService} from '../security-scan.service';
import {catchError, distinctUntilChanged, startWith, switchMap} from 'rxjs/operators';
import {LayoutDirective} from '../../../shared/layouts/layout.directive';
import {ISecurityScan} from '../security-scan-interfaces';
import {of, Subscription} from 'rxjs';
import {
  IClickListItem,
} from '../../../shared/charts/click-list/click-list-interfaces';
import {XByOneLayoutComponent} from '../../../shared/layouts/x-by-one-layout/x-by-one-layout.component';
import {DashStatus} from '../../../shared/dash-status/DashStatus';
import {WidgetState} from '../../../shared/widget-header/widget-state';
import { IChart } from 'src/app/shared/interfaces';
import {ClickListComponent} from '../../../shared/charts/click-list/click-list.component';
import { SecurityScanDetailComponent } from '../security-scan-detail/security-scan-detail.component';

@Component({
  selector: 'app-security-scan-widget',
  templateUrl: './security-scan-widget.component.html',
  styleUrls: ['./security-scan-widget.component.scss']
})
export class SecurityScanWidgetComponent extends WidgetComponent implements OnInit, AfterViewInit, OnDestroy {

  private intervalRefreshSubscription: Subscription;
  private params;

  @ViewChild(LayoutDirective, {static: true}) childLayoutTag: LayoutDirective;

  constructor(componentFactoryResolver: ComponentFactoryResolver,
              cdr: ChangeDetectorRef,
              dashboardService: DashboardService,
              private securityService: SecurityScanService) {
    super(componentFactoryResolver, cdr, dashboardService);
  }

  ngOnInit() {
    this.widgetId = 'codeanalysis0';
    this.layout = XByOneLayoutComponent;
    this.charts = [];
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
          this.widgetConfigExists = false;
          return of([]);
        }
        this.widgetConfigExists = true;
        // check if collector item type is tied to dashboard
        // if true, set state to READY, otherwise keep at default CONFIGURE
        if (this.dashboardService.checkCollectorItemTypeExist('StaticSecurityScan')) {
          this.state = WidgetState.READY;
        }
        this.params = {
          componentId: widgetConfig.componentId,
          max: 1
        };
        return this.securityService.getSecurityScanDetails(this.params.componentId)
          .pipe(catchError(err => of(err)));
        // );
      })).subscribe(result => {
        this.hasData = (result && result.length > 0);
        if (this.hasData) {
          this.loadCharts(result);
        } else {
          this.setDefaultIfNoData();
        }
        super.loadComponent(this.childLayoutTag);
      });

    // for quality widget, subscribe to updates from other quality components
    this.dashboardService.dashboardQualityConfig$.subscribe(result => {
      if (result) {
        this.widgetConfigSubject.next(result);
      } else {
        this.widgetConfigSubject.next();
      }
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
    const sData = [];

    result.forEach(scan => {
      const projectMetrics = [];
      scan.metrics.map(metric => {
        const riskStatus = metric.name === 'High' ? DashStatus.CRITICAL : (metric.name === 'Medium' ?
        DashStatus.WARN : DashStatus.PASS);
        const clickListItem = {
          title: metric.name,
          subtitles : [metric.value],
          status: riskStatus,
          statusText: metric.status,
        } as IClickListItem;
        projectMetrics.push(clickListItem);
      });
      const projectInfo = { name: scan.name, url: scan.url ? scan.url : '', timestamp: scan.timestamp, metrics: projectMetrics };
      sData.push(projectInfo);
    });

    this.populateChartsFromData(sData);
  }

  populateChartsFromData(sData) {
    for (const project of sData) {
      const chart: IChart = {
        title: project.name,
        component: ClickListComponent,
        data: {
          name: project.name,
          items: project.metrics,
          clickableContent: null,
          url: project.url,
          timestamp: project.timestamp,
          clickableHeader: SecurityScanDetailComponent
        } ,
        xAxisLabel: '',
        yAxisLabel: '',
        colorScheme: {}
      };
      this.charts.push(chart);
    }
  }

  setDefaultIfNoData() {
    if (!this.hasData) {
      this.charts[0].data = { items: [{ title: 'No Data Found' }]};
    }
    super.loadComponent(this.childLayoutTag);
  }

}
