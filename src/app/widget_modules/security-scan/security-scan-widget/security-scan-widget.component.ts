import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ComponentFactoryResolver,
  OnDestroy,
  OnInit,
  ViewChild,
  ElementRef
} from '@angular/core';
import { WidgetComponent } from '../../../shared/widget/widget.component';
import { DashboardService } from '../../../shared/dashboard.service';
import { SecurityScanService } from '../security-scan.service';
import { catchError, distinctUntilChanged, startWith, switchMap } from 'rxjs/operators';
import { LayoutDirective } from '../../../shared/layouts/layout.directive';
import { ISecurityScan } from '../security-scan-interfaces';
import { of, Subscription } from 'rxjs';
import {
  IClickListItemMetric,
} from '../../../shared/charts/click-list/click-list-interfaces';
import { OneChartLayoutComponent } from '../../../shared/layouts/one-chart-layout/one-chart-layout.component';
import { DashStatus } from '../../../shared/dash-status/DashStatus';
import { WidgetState } from '../../../shared/widget-header/widget-state';
import { IChart } from 'src/app/shared/interfaces';
import { ClickListComponent } from '../../../shared/charts/click-list/click-list.component';
import { SecurityScanDetailComponent } from '../security-scan-detail/security-scan-detail.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { RefreshModalComponent } from '../../../shared/modals/refresh-modal/refresh-modal.component';
import { ICollItem } from 'src/app/viewer_modules/collector-item/interfaces';
import { SecurityScanMetricDetailComponent } from '../security-scan-metric-detail/security-scan-metric-detail.component';


@Component({
  selector: 'app-security-scan-widget',
  templateUrl: './security-scan-widget.component.html',
  styleUrls: ['./security-scan-widget.component.scss']
})
export class SecurityScanWidgetComponent extends WidgetComponent implements OnInit, AfterViewInit, OnDestroy {

  private intervalRefreshSubscription: Subscription;
  private params;
  public hasData;
  public allCollectorItems;
  public loading: boolean;
  private selectedIndex: number;
  public hasRefreshLink: boolean;
  @ViewChild('projectSelector', { static: true }) projectSelector: ElementRef;
  @ViewChild(LayoutDirective, { static: true }) childLayoutTag: LayoutDirective;

  constructor(componentFactoryResolver: ComponentFactoryResolver,
              cdr: ChangeDetectorRef,
              dashboardService: DashboardService,
              private modalService: NgbModal,
              private securityService: SecurityScanService) {
              super(componentFactoryResolver, cdr, dashboardService);
  }

  ngOnInit() {
    this.widgetId = 'codeanalysis0';
    this.layout = OneChartLayoutComponent;
    this.charts = [];
    this.auditType = 'STATIC_SECURITY_ANALYSIS';
    this.allCollectorItems = [];
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
        return this.securityService.getSecurityScanCollectorItems(this.params.componentId)
          .pipe(catchError(err => of(err)));
        // );
      })).subscribe(result => {
        this.hasData = (result && result.length > 0);
        if (this.hasData) {
          this.loadCharts(result, 0);
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

  loadCharts(result: ICollItem[], index) {
    this.selectedIndex = index;
    if ( result[this.selectedIndex].refreshLink ) {
      this.hasRefreshLink = true;
    } else {
      this.hasRefreshLink = false;
    }
    this.populateDropdown(result);
    const collectorItemId = result[index].id;
    this.securityService.getCodeQuality(this.params.componentId, collectorItemId).subscribe(codeQuality => {
      this.generateSecurityScanData(codeQuality.result[0], index);
    });
  }

  populateDropdown(collectorItems) {
    collectorItems.map(item => {
      if (item.description) {
        item.description = item.description.split(':')[0];
      }
    });
    this.allCollectorItems = collectorItems;
  }


  generateSecurityScanData(codeQuality: ISecurityScan, index) {
    this.charts = [];
    const projectMetrics = [];
    codeQuality.metrics.map(metric => {
      const riskStatus = metric.name === 'High' ? DashStatus.CRITICAL : (metric.name === 'Medium' ?
        DashStatus.WARN : DashStatus.PASS);
      const clickListItem = {
        title: metric.name,
        subtitles: [metric.value],
        status: riskStatus,
        statusText: metric.status,
        instances: metric.instances
      } as IClickListItemMetric;
      projectMetrics.push(clickListItem);
    });

    const currentItem = this.allCollectorItems[index];
    const projectInfo = {
      description: currentItem.description, reportUrl: currentItem.options.reportUrl ? currentItem.options.reportUrl : '',
      timestamp: codeQuality.timestamp, metrics: projectMetrics
    };

    this.populateChartsFromData(projectInfo);
  }

  populateChartsFromData(projectInfo) {
    const currentChart: IChart = {
      title: projectInfo.description,
      component: ClickListComponent,
      data: {
        name: projectInfo.description,
        items: projectInfo.metrics,
        url: projectInfo.reportUrl,
        timestamp: projectInfo.timestamp,
        clickableHeader: SecurityScanDetailComponent,
        clickableContent: SecurityScanMetricDetailComponent
      },
      xAxisLabel: '',
      yAxisLabel: '',
      colorScheme: {}
    };
    this.charts = [currentChart];
    super.loadComponent(this.childLayoutTag);

  }

  setDefaultIfNoData() {
    this.allCollectorItems = [];
    if (!this.hasData) {
      const defaultItem: IChart = {
        title: 'Security Scan',
        component: ClickListComponent,
        data: [],
        xAxisLabel: '',
        yAxisLabel: '',
        colorScheme: {}
      };
      this.charts.push(defaultItem);
      this.charts[0].data = { items: [{ title: 'No Data Found' }] };
    }
    super.loadComponent(this.childLayoutTag);
  }

  refreshProject() {
    const refreshLink = this.allCollectorItems[this.selectedIndex].refreshLink;

    // Redundant check for refresh link, but just in case somebody attempts to call refreshProject() without hitting the button
    if ( !this.hasData || !refreshLink   ) {
      return;
    }

    this.loading = true;

    this.securityService.refreshProject(refreshLink).subscribe(refreshResult => {
      this.loading = false;
      const modalRef = this.modalService.open(RefreshModalComponent);
      modalRef.componentInstance.message = refreshResult;
      modalRef.componentInstance.title = this.charts[0].title;
      modalRef.result.then(modalResult => {
        this.reloadAfterRefresh();
      });
    }, err => {
      console.log(err);
      this.loading = false;
      const modalRef = this.modalService.open(RefreshModalComponent);
      modalRef.componentInstance.message = 'Something went wrong while trying to refresh the data.';
      modalRef.componentInstance.title = this.charts[0].title;
      modalRef.result.then(modalResult => {
        this.reloadAfterRefresh();
      });

    });
  }

  reloadAfterRefresh() {
    this.securityService.getSecurityScanCollectorItems(this.params.componentId).subscribe(result => {
      this.hasData = (result && result.length > 0);
      if (this.hasData) {
        this.loadCharts(result, this.selectedIndex);
      } else {
        // Select the first option in the dropdown since there will only be the default option.
        this.selectedIndex = 0;
        this.setDefaultIfNoData();
      }
      super.loadComponent(this.childLayoutTag);
      this.hasRefreshLink =  true;
      this.projectSelector.nativeElement.selectedIndex =  this.selectedIndex;
    });
  }

}
