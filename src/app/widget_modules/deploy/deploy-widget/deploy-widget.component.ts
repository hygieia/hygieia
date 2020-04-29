import {Component, OnInit, ChangeDetectorRef, ViewChild} from '@angular/core';
import {
  IClickListData,
  IClickListItemDeploy
} from 'src/app/shared/charts/click-list/click-list-interfaces';
import { DeployService } from 'src/app/widget_modules/deploy/deploy.service';
import { of, Subscription } from 'rxjs';
import { DEPLOY_CHARTS } from 'src/app/widget_modules/deploy/deploy-widget/deploy-charts';
import { IDeploy } from 'src/app/widget_modules/deploy/interfaces';
import { DashStatus } from 'src/app/shared/dash-status/DashStatus';
import { DashboardService } from 'src/app/shared/dashboard.service';
import { DeployDetailComponent } from 'src/app/widget_modules/deploy/deploy-detail/deploy-detail.component';
import { WidgetComponent } from 'src/app/shared/widget/widget.component';
import { ActivatedRoute } from '@angular/router';
import { ComponentFactoryResolver } from '@angular/core';
import { startWith, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { LayoutDirective } from 'src/app/shared/layouts/layout.directive';
import { OneChartLayoutComponent } from 'src/app/shared/layouts/one-chart-layout/one-chart-layout.component';

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
              route: ActivatedRoute,
              private deployService: DeployService) {
    super(ComponentFactoryResolver, cdr, dashboardService, route);
  }
  charts: any;
  widgetId: string;
  layout: typeof OneChartLayoutComponent;
  private TimeThreshold: number;

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
        this.TimeThreshold = 1000 * 60 * widgetConfig.options.deployDurationThreshold;
        return this.deployService.fetchDetails(widgetConfig.componentId);
      })).subscribe(result => {
      if (result) {
        this.loadCharts(result);
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
    if (!result) {
      return;
    }

    const sorted = result.sort((a: IDeploy, b: IDeploy): number => {
      return a.units[0].lastUpdated - b.units[0].lastUpdated;
    }).reverse().slice(0, 5);
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
      clickableHeader: null
    } as IClickListData;
  }
}
