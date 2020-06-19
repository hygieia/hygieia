import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ComponentFactoryResolver,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {forkJoin, of, Subscription} from 'rxjs';
import {catchError, distinctUntilChanged, startWith, switchMap} from 'rxjs/operators';
import {
  IClickListData,
  IClickListItem, IClickListItemFeature
} from 'src/app/shared/charts/click-list/click-list-interfaces';
import {DashboardService} from 'src/app/shared/dashboard.service';
import {LayoutDirective} from 'src/app/shared/layouts/layout.directive';
import {TwoByTwoLayoutComponent} from 'src/app/shared/layouts/two-by-two-layout/two-by-two-layout.component';
import {WidgetComponent} from 'src/app/shared/widget/widget.component';
import {FeatureService} from '../feature.service';
import {IFeature} from '../interfaces';
import {FEATURE_CHARTS} from './feature-charts';
import {FeatureDetailComponent} from '../feature-detail/feature-detail.component';
import {WidgetState} from '../../../shared/widget-header/widget-state';

@Component({
  selector: 'app-feature-widget',
  templateUrl: './feature-widget.component.html',
  styleUrls: ['./feature-widget.component.scss']
})
export class FeatureWidgetComponent extends WidgetComponent implements OnInit, AfterViewInit, OnDestroy {

  private params;
  // Reference to the subscription used to refresh the widget
  private intervalRefreshSubscription: Subscription;

  @ViewChild(LayoutDirective, {static: false}) childLayoutTag: LayoutDirective;

  constructor(componentFactoryResolver: ComponentFactoryResolver,
              cdr: ChangeDetectorRef,
              dashboardService: DashboardService,
              route: ActivatedRoute,
              private featureService: FeatureService) {
    super(componentFactoryResolver, cdr, dashboardService, route);
  }

  // Initialize the widget and set layout and charts.
  ngOnInit() {
    this.widgetId = 'feature0';
    this.layout = TwoByTwoLayoutComponent;
    // Chart configuration moved to external file
    this.charts = FEATURE_CHARTS;
    this.auditType = '';
    this.init();
  }

  // After the view is ready start the refresh interval.
  ngAfterViewInit() {
    this.startRefreshInterval();
  }

  ngOnDestroy() {
    this.stopRefreshInterval();
  }

  // Start a subscription to the widget configuration for this widget and refresh the graphs each
  // cycle.
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
        this.params = {
          id: widgetConfig.options.id,
          featureTool: widgetConfig.options.featureTool,
          teamName: widgetConfig.options.teamName,
          projectName: widgetConfig.options.projectName,
          component: widgetConfig.componentId,
          teamId: widgetConfig.options.teamId,
          projectId: widgetConfig.options.projectId,
          sprintType: widgetConfig.options.sprintType,
          listType: widgetConfig.options.listType,
        };

        return forkJoin(
          this.featureService.fetchFeatureWip(this.params.component, this.params.teamId, this.params.projectId,
            this.params.sprintType).pipe(catchError(err => of(err))),
          this.featureService.fetchAggregateSprintEstimates(this.params.component, this.params.teamId,
            this.params.projectId, this.params.sprintType).pipe(catchError(err => of(err))),
          this.featureService.fetchIterations(this.params.component, this.params.teamId, this.params.projectId,
            this.params.sprintType).pipe(catchError(err => of(err))));
      })).subscribe(([wip, estimates, iterations]) => {
        this.hasData = ((wip && (wip as []).length > 0) ||
          (estimates && (estimates as []).length > 0) ||
          (iterations && (iterations as []).length > 0));
        if (this.hasData) {
          this.loadCharts(wip, estimates, iterations);
        } else {
          this.setDefaultIfNoData();
        }
      });
  }

  loadCharts(wip, estimates: IFeature, iterations) {
    if (this.params.listType === 'epics') {
      this.generateFeatureSummary(wip, this.params);
    } else {
      this.generateFeatureSummary(iterations, this.params);
    }
    this.generateIterationSummary(estimates);
    super.loadComponent(this.childLayoutTag);
  }

  // Unsubscribe from the widget refresh observable, which stops widget updating.
  stopRefreshInterval() {
    if (this.intervalRefreshSubscription) {
      this.intervalRefreshSubscription.unsubscribe();
    }
  }

  // ********************** FEATURE SUMMARY ***************************

  generateFeatureSummary(content, params) {
    if (!content) {
      return;
    }

    const items = [
      {
        status: null,
        statusText: '',
        title: 'Feature Tool',
        subtitles: [params.featureTool],
      },
      {
        status: null,
        statusText: '',
        title: 'Project Name',
        subtitles: [params.projectName],
      },
      {
        status: null,
        statusText: '',
        title: 'Team Name',
        subtitles: [params.teamName],
      },
    ] as IClickListItem[];

    if (params.listType === 'issues') {
      items[3] = {
        status: null,
        statusText: '',
        title: 'Backlog items',
        subtitles: [content.filter(curr => curr.sStatus === 'Backlog').length]
      } as IClickListItem;

      items[4] = {
        status: null,
        statusText: '',
        title: 'In Progress items',
        subtitles: [content.filter(curr => curr.sStatus === 'In Progress').length]
      } as IClickListItem;

      items[5] = {
        status: null,
        statusText: '',
        title: 'Done items',
        subtitles: [content.filter(curr => curr.sStatus === 'Done').length]
      } as IClickListItem;
    }

    this.processFeatureWipResponse(content as IClickListItemFeature, params.listType);
    this.charts[0].data = {
      items,
      clickableContent: null,
      clickableHeader: null
    } as IClickListData;
  }

  // *********************** ITERATION SUMMARY ************************

  // Displays Sprint information for Open, WIP, Done
  generateIterationSummary(result: IFeature) {
    let items;
    if (!result) {
      return;
    }

    if (this.params.sprintType === 'scrum' || this.params.sprintType === 'both') {
      items = [
        {
          status: null,
          statusText: '',
          title: 'OPEN',
          subtitles: [result.openEstimate],
        },
        {
          status: null,
          statusText: '',
          title: 'WIP',
          subtitles: [result.inProgressEstimate],
        },
        {
          status: null,
          statusText: '',
          title: 'DONE',
          subtitles: [result.completeEstimate],
        },
      ] as IClickListItem[];
    } else if (this.params.sprintType === 'kanban') {
      items = [
        {
          status: null,
          statusText: '',
          title: 'OPEN',
          subtitles: [result.openEstimate],
        },
        {
          status: null,
          statusText: '',
          title: 'WIP',
          subtitles: [result.inProgressEstimate],
        }
      ] as IClickListItem[];
    }

    this.charts[1].data = {
      items,
      clickableContent: null,
      clickableHeader: null
    } as IClickListData;
  }

  // **************************** EPICS/ISSUES *******************************

  // Displays epics or issues
  private processFeatureWipResponse(data, issueOrEpic: string) {
    let issueOrEpicCollection: IClickListItemFeature[] = [];

    if (issueOrEpic === 'issues') {
      issueOrEpicCollection = data.sort((a: IClickListItemFeature, b: IClickListItemFeature): number => {
        return a.changeDate > b.changeDate ? 1 : -1;
      }).reverse().slice(0, 10);
    } else {
      data.forEach(curr => {
        issueOrEpicCollection.push(curr);
      });
    }

    const items = issueOrEpicCollection.map(curr => {
      if (issueOrEpic === 'epics') {
        return {
          title: curr.sEpicName,
          name: curr.sEpicName,
          url: curr.sEpicUrl,
          number: curr.sEpicNumber,
          progressStatus: '-',
          type: 'Epic',
          date: '-',
          time: curr.sEstimate
        } as IClickListItemFeature;
      } else {
        const regexText = curr.changeDate.match(new RegExp('^([^T]*);*'))[0];
        return {
          title: curr.sName,
          name: curr.sName,
          url: curr.sUrl,
          number: curr.sNumber,
          progressStatus: curr.sStatus,
          type: 'Issue',
          date: regexText,
          time: curr.sEstimateTime
        } as IClickListItemFeature;
      }
    });

    this.charts[2].data = {
      items,
      clickableContent: FeatureDetailComponent,
      clickableHeader: null
    } as IClickListData;
  }

  setDefaultIfNoData() {
    if (!this.hasData) {
      this.charts[0].data = { items: [{ title: 'No Data Found' }]};
      this.charts[1].data = { items: [{ title: 'No Data Found' }]};
      this.charts[2].data = { items: [{ title: 'No Data Found' }]};
    }
    super.loadComponent(this.childLayoutTag);
  }

}
