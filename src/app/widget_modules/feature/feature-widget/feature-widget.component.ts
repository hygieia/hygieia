import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ComponentFactoryResolver,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import {forkJoin, of, Subscription} from 'rxjs';
import {catchError, distinctUntilChanged, startWith, switchMap} from 'rxjs/operators';
import {
  IClickListData,
  IClickListItem
} from 'src/app/shared/charts/click-list/click-list-interfaces';
import {DashboardService} from 'src/app/shared/dashboard.service';
import {LayoutDirective} from 'src/app/shared/layouts/layout.directive';
import {WidgetComponent} from 'src/app/shared/widget/widget.component';
import {FeatureService} from '../feature.service';
import {IFeature} from '../interfaces';
import {FEATURE_CHARTS} from './feature-charts';
import {WidgetState} from '../../../shared/widget-header/widget-state';
import {IRotationData, IFeatureRotationItem} from '../../../shared/charts/rotation/rotation-chart-interfaces';
import {FeatureDetailComponent} from '../feature-detail/feature-detail.component';
import {TwoByOneLayoutComponent} from '../../../shared/layouts/two-by-one-layout/two-by-one-layout.component';

@Component({
  selector: 'app-feature-widget',
  templateUrl: './feature-widget.component.html',
  styleUrls: ['./feature-widget.component.scss']
})
export class FeatureWidgetComponent extends WidgetComponent implements OnInit, AfterViewInit, OnDestroy {

  private params;
  // Reference to the subscription used to refresh the widget
  private intervalRefreshSubscription: Subscription;
  private backlog = [];
  private inProg = [];
  private done = [];
  private featureWip;

  @ViewChild(LayoutDirective, {static: false}) childLayoutTag: LayoutDirective;

  constructor(componentFactoryResolver: ComponentFactoryResolver,
              cdr: ChangeDetectorRef,
              dashboardService: DashboardService,
              private featureService: FeatureService) {
    super(componentFactoryResolver, cdr, dashboardService);
  }

  // Initialize the widget and set layout and charts.
  ngOnInit() {
    this.widgetId = 'feature0';
    this.layout = TwoByOneLayoutComponent;
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
            'scrum').pipe(catchError(err => of(err))),
          this.featureService.fetchFeatureWip(this.params.component, this.params.teamId, this.params.projectId,
            'kanban').pipe(catchError(err => of(err))),
          this.featureService.fetchAggregateSprintEstimates(this.params.component, this.params.teamId,
            this.params.projectId, 'scrum').pipe(catchError(err => of(err))),
          this.featureService.fetchAggregateSprintEstimates(this.params.component, this.params.teamId,
            this.params.projectId, 'kanban').pipe(catchError(err => of(err))),
          this.featureService.fetchIterations(this.params.component, this.params.teamId, this.params.projectId,
            'scrum').pipe(catchError(err => of(err))),
          this.featureService.fetchIterations(this.params.component, this.params.teamId, this.params.projectId,
            'kanban').pipe(catchError(err => of(err))));
      })).subscribe(([wipScrum, wipKanban, estimatesScrum, estimatesKanban, iterationsScrum, iterationsKanban]) => {
      this.loadCharts([wipScrum, wipKanban], [estimatesScrum, estimatesKanban], [iterationsScrum, iterationsKanban]);
    });
  }

  loadCharts(wipArray, estimatesArray: IFeature[], iterationsArray) {
    if (this.params.listType === 'epics') {
      this.generateFeatureSummary(wipArray, this.params);
    } else {
      this.generateFeatureSummary(iterationsArray, this.params);
    }
    this.generateIterationSummary(estimatesArray);
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
        subtitles: [ typeof(params.projectName) === 'string' ? params.projectName : params.projectName.options.projectName],
      },
      {
        status: null,
        statusText: '',
        title: 'Team Name',
        subtitles: [ typeof(params.teamName) === 'string' ? params.teamName : params.teamName.options.teamName],
      },
    ] as IClickListItem[];

    if (params.listType === 'issues') {
      content.forEach(currSprintType => {
        this.backlog.push(currSprintType.filter(curr => curr.sStatus === 'Backlog').length);
        this.inProg.push(currSprintType.filter(curr => curr.sStatus === 'In Progress').length);
        this.done.push(currSprintType.filter(curr => curr.sStatus === 'Done').length);
      });
    }
    this.featureWip = this.processFeatureWipResponse(content as IFeatureRotationItem, params.listType);
    this.charts[1].data = {
      items,
      clickableContent: null,
      clickableHeader: null
    } as IClickListData;
  }

  // *********************** ITERATION SUMMARY ************************

  // Displays Sprint information for Open, WIP, Done
  generateIterationSummary(result: IFeature[]) {
    let scrumItems;
    let kanbanItems;

    if (!result) {
      return;
    }
    scrumItems = [
      {
        agileType: this.params.sprintType,
        type: 'Scrum',
        title: 'OPEN',
        subtitles: [result[0].openEstimate],
        status: [{Backlog: this.backlog[0], 'In Progress': this.inProg[0], Done: this.done[0]}],
        rotationData: this.featureWip[0]
      },
      {
        agileType: this.params.sprintType,
        type: 'Scrum',
        title: 'WIP',
        subtitles: [result[0].inProgressEstimate],
        rotationData: this.featureWip[0],
      },
      {
        agileType: this.params.sprintType,
        type: 'Scrum',
        title: 'DONE',
        subtitles: [result[0].completeEstimate],
        rotationData: this.featureWip[0],
      },
    ] as IFeatureRotationItem[];

    kanbanItems = [
      {
        agileType: this.params.sprintType,
        type: 'Kanban',
        title: 'OPEN',
        subtitles: [result[1].openEstimate],
        status: [{Backlog: this.backlog[1], 'In Progress': this.inProg[1], Done: this.done[1]}],
        rotationData: this.featureWip[1]
      },
      {
        agileType: this.params.sprintType,
        type: 'Kanban',
        title: 'WIP',
        subtitles: [result[1].inProgressEstimate],
        rotationData: this.featureWip[1],
      }
    ] as IFeatureRotationItem[];

    if (this.params.sprintType === 'scrumkanban') {
      this.charts[0].data = {
        items: [scrumItems, kanbanItems],
        clickableContent: FeatureDetailComponent,
        clickableHeader: null
      } as IRotationData;
    } else if (this.params.sprintType === 'scrum') {
      this.charts[0].data = {
        items: [scrumItems],
        clickableContent: FeatureDetailComponent,
        clickableHeader: null
      } as IRotationData;
    } else {
      this.charts[0].data = {
        items: [kanbanItems],
        clickableContent: FeatureDetailComponent,
        clickableHeader: null
      } as IRotationData;
    }
  }

  // **************************** EPICS/ISSUES *******************************

  private processFeatureWipResponse(data, issueOrEpic: string) {
    const items = [this.issueOrEpicBreakdown(data[0], issueOrEpic), this.issueOrEpicBreakdown(data[1], issueOrEpic)];
    return items;
  }

  private issueOrEpicBreakdown(issueOrEpicCollection, issueOrEpic) {
    if (issueOrEpic === 'issues') {
      issueOrEpicCollection = issueOrEpicCollection.sort((a: IFeatureRotationItem, b: IFeatureRotationItem): number => {
        return a.changeDate > b.changeDate ? 1 : -1;
      }).reverse().slice(0, 10);
    }
    return issueOrEpicCollection.map(curr => {
      if (issueOrEpic === 'epics') {
        return {
          title: curr.sEpicName,
          name: curr.sEpicName,
          url: curr.sEpicUrl,
          number: curr.sEpicNumber,
          progressStatus: 'N/A',
          type: 'Epic',
          date: 'N/A',
          time: curr.sEstimate
        } as IFeatureRotationItem;
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
        } as IFeatureRotationItem;
      }
    });
  }
}
