import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ComponentFactoryResolver,
  ElementRef,
  OnDestroy,
  OnInit,
  ViewChild
} from '@angular/core';
import {WidgetComponent} from '../../../shared/widget/widget.component';
import {DashboardService} from '../../../shared/dashboard.service';
import {OpensourceScanService} from '../opensource-scan.service';
import {catchError, distinctUntilChanged, startWith, switchMap} from 'rxjs/operators';
import {of, Subscription} from 'rxjs';
import {LayoutDirective} from '../../../shared/layouts/layout.directive';
import {OSS_CHARTS} from './oss-charts';
import {DashStatus} from '../../../shared/dash-status/DashStatus';
import {
  IClickListData,
  IClickListItemOSS
} from '../../../shared/charts/click-list/click-list-interfaces';
import {OSSDetailComponent} from '../oss-detail/oss-detail.component';
import {TwoByOneLayoutComponent} from '../../../shared/layouts/two-by-one-layout/two-by-one-layout.component';
import {IOpensourceScan, IThreat} from '../interfaces';
import {OSSDetailAllComponent} from '../oss-detail-all/oss-detail-all.component';
import {WidgetState} from '../../../shared/widget-header/widget-state';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ICollItem } from 'src/app/viewer_modules/collector-item/interfaces';
import { RefreshModalComponent } from '../../../shared/modals/refresh-modal/refresh-modal.component';



@Component({
  selector: 'app-oss-widget',
  templateUrl: './oss-widget.component.html',
  styleUrls: ['./oss-widget.component.scss']
})
export class OSSWidgetComponent extends WidgetComponent implements OnInit, AfterViewInit, OnDestroy {

  // private readonly OSS_MAX_CNT = 1;

  // Reference to the subscription used to refresh the widget
  private intervalRefreshSubscription: Subscription;
  private params;
  public allCollectorItems;
  public loading: boolean;
  private selectedIndex: number;
  public hasRefreshLink: boolean;
  @ViewChild('projectSelector', { static: true }) projectSelector: ElementRef;
  @ViewChild(LayoutDirective, {static: false}) childLayoutTag: LayoutDirective;

  constructor(componentFactoryResolver: ComponentFactoryResolver,
              cdr: ChangeDetectorRef,
              dashboardService: DashboardService,
              private modalService: NgbModal,
              private ossService: OpensourceScanService) {
    super(componentFactoryResolver, cdr, dashboardService);
  }

  ngOnInit() {
    this.widgetId = 'codeanalysis0';
    this.layout = TwoByOneLayoutComponent;
    this.charts = OSS_CHARTS;
    this.auditType = 'LIBRARY_POLICY';
    this.allCollectorItems = [];
    this.init();
  }

  // After the view is ready start the refresh interval.
  ngAfterViewInit() {
    this.startRefreshInterval();
  }

  ngOnDestroy() {
    this.stopRefreshInterval();
  }

  // Start a subscription to the widget configuration for this widget
  // and refresh the charts
  startRefreshInterval() {
    this.intervalRefreshSubscription = this.dashboardService.dashboardRefresh$.pipe(
      startWith(-1), // Refresh this widget seperate from dashboard (ex. config is updated)
      distinctUntilChanged(), // If dashboard is loaded the first time, ignore widget double refresh
      switchMap(_ => this.getCurrentWidgetConfig()),
      switchMap(widgetConfig => {
        if (!widgetConfig) {
          this.widgetConfigExists = false;
          return of([]);
        }
        this.widgetConfigExists = true;
        // check if collector item type is tied to dashboard
        // if true, set state to READY, otherwise keep at default CONFIGURE
        if (this.dashboardService.checkCollectorItemTypeExist('LibraryPolicy')) {
          this.state = WidgetState.READY;
        }
        this.params = {
          componentId: widgetConfig.componentId,
          max: 1
        };
        return this.ossService.getLibraryPolicyCollectorItems(widgetConfig.componentId).pipe(catchError(err => of(err)));
      })).subscribe(result => {
        this.hasData = result && result.length > 0;
        if (this.hasData) {
          this.loadCharts(result, 0);
        } else {
          this.setDefaultIfNoData();
        }
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

  // Unsubscribe from the widget refresh observable, which stops widget updating.
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

    this.ossService.fetchDetails(this.params.componentId, collectorItemId).subscribe(libraryPolicy => {
      if (libraryPolicy.length > 0) {
        this.generateLicenseDetails(libraryPolicy[0]);
        this.generateSecurityDetails(libraryPolicy[0]);
        super.loadComponent(this.childLayoutTag);
      } else {
        this.setDefaultIfNoData();
      }

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

  generateLicenseDetails(result: IOpensourceScan) {
    if (!result || !result.threats || !result.threats.License) {
      this.charts[0].data = [];
      return;
    }

    let count = 0;
    let openCount = 0;

    const sorted = result.threats.License.sort((a: IThreat, b: IThreat): number => {
      return this.getDashStatus(a.level) - this.getDashStatus(b.level);
    }).reverse();

    const latestDetails = sorted.map(oss => {
        const ossStatus = this.getDashStatus(oss.level);
        const open = (oss.dispositionCounts.Open) ? oss.dispositionCounts.Open : 0;
        const ossStatusTitle = oss.level + ' (' + open + '/' + oss.count + ')';
        count += oss.count;
        openCount += open;

        return {
          status: ossStatus,
          statusText: oss.level,
          title: ossStatusTitle,
          subtitles: [],
          url: result.reportUrl,
          components: oss.components,
          lastUpdated: result.timestamp
        } as IClickListItemOSS;
      }
    );

    this.charts[0].title = 'License (' + openCount + '/' + count + ')';
    this.charts[0].data = {
      items: latestDetails,
      clickableContent: OSSDetailComponent,
      clickableHeader: OSSDetailAllComponent
    } as IClickListData;
  }


  generateSecurityDetails(result: IOpensourceScan) {
    if (!result || !result.threats || !result.threats.Security) {
      this.charts[1].data = [];
      return;
    }

    let count = 0;
    let openCount = 0;

    const sorted = result.threats.Security.sort((a: IThreat, b: IThreat): number => {
      return this.getDashStatus(a.level) - this.getDashStatus(b.level);
    }).reverse();

    const latestDetails = sorted.map(oss => {
        const ossStatus = this.getDashStatus(oss.level);
        const open = (oss.dispositionCounts.Open) ? oss.dispositionCounts.Open : 0;
        const ossStatusTitle = oss.level + ' (' + open + '/' + oss.count + ')';
        count += oss.count;
        openCount += open;

        return {
          status: ossStatus,
          statusText: oss.level,
          title: ossStatusTitle,
          subtitles: [],
          url: result.reportUrl,
          components: oss.components,
          lastUpdated: result.timestamp
        } as IClickListItemOSS;
      }
    );

    this.charts[1].title = 'Security (' + openCount + '/' + count + ')';
    this.charts[1].data = {
      items: latestDetails,
      clickableContent: OSSDetailComponent,
      clickableHeader: OSSDetailAllComponent
    } as IClickListData;
  }

  getDashStatus(level: string) {
    switch (level.toLowerCase()) {
      case 'critical':
        return DashStatus.CRITICAL;
      case 'high':
        return DashStatus.UNAUTH;
      case 'medium':
        return DashStatus.WARN;
      case 'low' :
        return DashStatus.IN_PROGRESS;
      default:
        return DashStatus.PASS;
    }
  }

  setDefaultIfNoData() {
    if (!this.hasData) {
      this.charts[0].data = { items: [{ title: 'No Data Found' }]};
      this.charts[1].data = { items: [{ title: 'No Data Found' }]};
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

    this.ossService.refreshProject(refreshLink).subscribe(refreshResult => {
      this.loading = false;
      const modalRef = this.modalService.open(RefreshModalComponent);
      modalRef.componentInstance.message = refreshResult;
      modalRef.componentInstance.title = this.projectSelector.nativeElement.value;
      modalRef.result.then(modalResult => {
        this.reloadAfterRefresh();
      });
    }, err => {
      console.log(err);
      this.loading = false;
      const modalRef = this.modalService.open(RefreshModalComponent);
      modalRef.componentInstance.message = 'Something went wrong while trying to refresh the data.';
      modalRef.componentInstance.title = this.allCollectorItems[this.selectedIndex].description;
      modalRef.result.then(modalResult => {
        this.reloadAfterRefresh();
      });

    });
  }

  reloadAfterRefresh() {
    this.ossService.getLibraryPolicyCollectorItems(this.params.componentId).subscribe(result => {
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
