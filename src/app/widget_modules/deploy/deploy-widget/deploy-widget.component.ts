import {ChangeDetectorRef, Component, ComponentFactoryResolver, OnInit, ViewChild} from '@angular/core';
import {IClickListData, IClickListItemDeploy} from 'src/app/shared/charts/click-list/click-list-interfaces';
import {DeployService} from 'src/app/widget_modules/deploy/deploy.service';
import {of, Subscription} from 'rxjs';
import {DEPLOY_CHARTS} from 'src/app/widget_modules/deploy/deploy-widget/deploy-charts';
import {IDeploy} from 'src/app/widget_modules/deploy/interfaces';
import {DashStatus} from 'src/app/shared/dash-status/DashStatus';
import {DashboardService} from 'src/app/shared/dashboard.service';
import {DeployDetailComponent} from 'src/app/widget_modules/deploy/deploy-detail/deploy-detail.component';
import {WidgetComponent} from 'src/app/shared/widget/widget.component';
import {distinctUntilChanged, startWith, switchMap} from 'rxjs/operators';
import {LayoutDirective} from 'src/app/shared/layouts/layout.directive';
import {OneChartLayoutComponent} from 'src/app/shared/layouts/one-chart-layout/one-chart-layout.component';
import {WidgetState} from '../../../shared/widget-header/widget-state';

@Component({
  selector: 'app-deploy-widget',
  templateUrl: './deploy-widget.component.html',
  styleUrls: ['./deploy-widget.component.scss']
})

export class DeployWidgetComponent extends WidgetComponent implements OnInit {
  // tslint:disable-next-line:no-shadowed-variable
  constructor(ComponentFactoryResolver: ComponentFactoryResolver,
              cdr: ChangeDetectorRef,
              dashboardService: DashboardService,
              private deployService: DeployService) {
    super(ComponentFactoryResolver, cdr, dashboardService);
  }
  charts: any;
  widgetId: string;
  layout: typeof OneChartLayoutComponent;
  // private TimeThreshold: number;

  // Default build time threshold
// Reference to the subscription used to refresh the widget
  private intervalRefreshSubscription: Subscription;

  // @ts-ignore
  @ViewChild(LayoutDirective) childLayoutTag: LayoutDirective;

  ngOnInit() {
    this.widgetId = 'deploy0';
    this.layout = OneChartLayoutComponent;
    // Chart configuration moved to external file
    this.charts = DEPLOY_CHARTS;
    this.auditType = 'DEPLOY';
    this.init();
  }

  // tslint:disable-next-line:use-lifecycle-interface
  ngAfterViewInit() {
    this.startRefreshInterval();
  }

  startRefreshInterval() {
    this.intervalRefreshSubscription = this.dashboardService.dashboardRefresh$.pipe(
      startWith(-1), // Refresh this widget seperate from dashboard (ex. config is updated)
      distinctUntilChanged(), // If dashboard is loaded the first time, ignore widget double refresh
      switchMap(_ => this.getCurrentWidgetConfig()),
      switchMap(widgetConfig => {
        if (!widgetConfig) {
          return of([]);
        }
        this.widgetConfigExists = true;
        this.state = WidgetState.READY;
        // this.TimeThreshold = 1000 * 60 * widgetConfig.options.deployDurationThreshold;
        return this.deployService.fetchDetails(widgetConfig.componentId);
      })).subscribe(result => {
        this.hasData = (result && result.length > 0);
        if (this.hasData) {
          this.loadCharts(result);
        } else {
          this.setDefaultIfNoData();
        }
    });
  }
  // Unsubscribe from the widget refresh observable, which stops widget updating.
  stopRefreshInterval() {
    if (this.intervalRefreshSubscription) {
      this.intervalRefreshSubscription.unsubscribe();
    }
  }

  loadCharts(result: IDeploy[]) {
    this.generateLatestDeployData(result);
    super.loadComponent(this.childLayoutTag);
  }

  generateLatestDeployData(result: IDeploy[]) {
    const sorted = result.sort((a: IDeploy, b: IDeploy): number => {
      return a.units[0].lastUpdated - b.units[0].lastUpdated;
    }).reverse().slice(0, 10);
    const latestDeployData = sorted.map(deploy => {
        let deployStatusText = '';
        let regexText = '';
        const deployStatus = deploy.units[0].deployed ?
          DashStatus.PASS : DashStatus.FAIL;
        if ( deployStatus === DashStatus.FAIL) {
          deployStatusText = '!';
        }

        if (deploy.url) {
          regexText = deploy.url.match(new RegExp('^(https?:\/\/)?(?:www.)?([^\/]+)'))[0];
        } else {
          regexText = 'N/A';
          deploy.url = '';
        }

        return {
          status: deployStatus,
          statusText: deployStatusText,
          title: deploy.name,
          subtitles: [],
          url: deploy.url,
          version: deploy.units[0].version,
          name: deploy.units[0].name,
          lastUpdated: deploy.units[0].lastUpdated,
          regex: regexText
        } as IClickListItemDeploy;
      }
    );

    this.charts[0].data = {
      items: latestDeployData,
      clickableContent: DeployDetailComponent,
      clickableHeader: DeployDetailComponent,
    } as IClickListData;
  }

  setDefaultIfNoData() {
    if (!this.hasData) {
      this.charts[0].data = { items: [{ title: 'No Data Found' }]};
    }
    super.loadComponent(this.childLayoutTag);
  }

}
