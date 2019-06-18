import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ComponentFactoryResolver,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { of, Subscription } from 'rxjs';
import { distinctUntilChanged, startWith, switchMap } from 'rxjs/operators';
import { IClickListData, IClickListItem } from 'src/app/shared/charts/click-list/click-list-interfaces';
import { DashStatus } from 'src/app/shared/dash-status/DashStatus';
import { DashboardService } from 'src/app/shared/dashboard.service';
import { LayoutDirective } from 'src/app/shared/layouts/layout.directive';
import { TwoByTwoLayoutComponent } from 'src/app/shared/layouts/two-by-two-layout/two-by-two-layout.component';
import { WidgetComponent } from 'src/app/shared/widget/widget.component';

import { BuildDetailComponent } from '../build-detail/build-detail.component';
import { BuildService } from '../build.service';
import { IBuild } from '../interfaces';
import { BUILD_CHARTS } from './build-charts';

@Component({
  selector: 'app-build-widget',
  templateUrl: './build-widget.component.html',
  styleUrls: ['./build-widget.component.scss']
})
export class BuildWidgetComponent extends WidgetComponent implements OnInit, AfterViewInit, OnDestroy {

  private readonly BUILDS_PER_DAY_TIME_RANGE = 14;
  private readonly TOTAL_BUILD_COUNTS_TIME_RANGES = [7, 14];

  private buildTimeThreshold: number;

  // Default build time threshold
  private readonly BUILD_TIME_THRESHOLD = 900000;

  // Reference to the subscription used to refresh the widget
  private intervalRefreshSubscription: Subscription;

  @ViewChild(LayoutDirective) childLayoutTag: LayoutDirective;

  constructor(componentFactoryResolver: ComponentFactoryResolver,
              cdr: ChangeDetectorRef,
              dashboardService: DashboardService,
              route: ActivatedRoute,
              private buildService: BuildService) {
    super(componentFactoryResolver, cdr, dashboardService, route);
  }

  // Initialize the widget and set layout and charts.
  ngOnInit() {
    this.widgetId = 'build0';
    this.layout = TwoByTwoLayoutComponent;
    // Chart configuration moved to external file
    this.charts = BUILD_CHARTS;
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
        this.buildTimeThreshold = 1000 * 60 * widgetConfig.options.buildDurationThreshold;
        return this.buildService.fetchDetails(widgetConfig.componentId, this.BUILDS_PER_DAY_TIME_RANGE);
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

  loadCharts(result: IBuild[]) {
    this.generateBuildsPerDay(result);
    this.generateTotalBuildCounts(result);
    this.generateAverageBuildDuration(result);
    this.generateLatestBuilds(result);
    super.loadComponent(this.childLayoutTag);
  }

  // *********************** BUILDS PER DAY ****************************
  private generateBuildsPerDay(result: IBuild[]) {
    const startDate = this.toMidnight(new Date());
    startDate.setDate(startDate.getDate() - this.BUILDS_PER_DAY_TIME_RANGE + 1);
    const allBuilds = result.filter(build => this.checkBuildAfterDate(build, startDate)
      && !this.checkBuildStatus(build, 'InProgress'));
    const failedBuilds = result.filter(build => this.checkBuildAfterDate(build, startDate)
      && !this.checkBuildStatus(build, 'InProgress') && !this.checkBuildStatus(build, 'Success'));
    this.charts[0].data.data[0].series = this.countBuildsPerDay(allBuilds, startDate);
    this.charts[0].data.data[1].series = this.countBuildsPerDay(failedBuilds, startDate);
  }

  private countBuildsPerDay(builds: IBuild[], startDate: Date): any[] {
    const counts = {};
    const date = new Date(startDate.getTime());
    for (let i = 0; i < this.BUILDS_PER_DAY_TIME_RANGE; i++) {
      counts[this.toMidnight(date).getTime()] = 0;
      date.setDate(date.getDate() + 1);
    }
    builds.forEach(build => {
      const index = this.toMidnight(new Date(build.endTime)).getTime();
      counts[index] = counts[index] + 1;
    });
    const dataArray = [];
    for (const key of Object.keys(counts)) {
      const data = counts[key];
      dataArray.push(
        {
          name: new Date(+key),
          value: data
        }
      );
    }
    return dataArray;
  }

  // *********************** LATEST BUILDS *****************************

  private generateLatestBuilds(result: IBuild[]) {
    const sorted = result.sort((a: IBuild, b: IBuild): number => {
      return a.endTime - b.endTime;
    }).reverse().slice(0, 5);

    const buildStatusTable = {
      success: DashStatus.PASS,
      inprogress: DashStatus.IN_PROGRESS
    };
    const latestBuildData = sorted.map(build => {
      let buildStatusText = '';
      const buildStatus = buildStatusTable[build.buildStatus.toLowerCase()] ?
        buildStatusTable[build.buildStatus.toLowerCase()] : DashStatus.FAIL;
      if (buildStatus === DashStatus.FAIL) {
        buildStatusText = '!';
      }
      return {
        status: buildStatus,
        statusText: buildStatusText,
        title: build.number,
        subtitles: [
          new Date(build.endTime)
        ],
        url: build.buildUrl
      } as IClickListItem;
    });
    this.charts[1].data = {
      items: latestBuildData,
      clickableContent: BuildDetailComponent,
      clickableHeader: null
    } as IClickListData;
  }

  // *********************** TOTAL BUILD COUNTS ************************

  private generateTotalBuildCounts(result: IBuild[]) {
    const today = this.toMidnight(new Date());
    const bucketOneStartDate = this.toMidnight(new Date());
    const bucketTwoStartDate = this.toMidnight(new Date());
    bucketOneStartDate.setDate(bucketOneStartDate.getDate() - this.TOTAL_BUILD_COUNTS_TIME_RANGES[0] + 1);
    bucketTwoStartDate.setDate(bucketTwoStartDate.getDate() - this.TOTAL_BUILD_COUNTS_TIME_RANGES[1] + 1);

    const todayCount = result.filter(build => this.checkBuildAfterDate(build, today)).length;
    const bucketOneCount = result.filter(build => this.checkBuildAfterDate(build, bucketOneStartDate)).length;
    const bucketTwoCount = result.filter(build => this.checkBuildAfterDate(build, bucketTwoStartDate)).length;

    this.charts[3].data[0].value = todayCount;
    this.charts[3].data[1].value = bucketOneCount;
    this.charts[3].data[2].value = bucketTwoCount;
  }

  // *********************** AVERAGE BUILD DURATION *********************

  private generateAverageBuildDuration(result: IBuild[]) {
    const startDate = this.toMidnight(new Date());
    // Get threshold from the configuration, or use default
    const threshold = this.buildTimeThreshold ? this.buildTimeThreshold : this.BUILD_TIME_THRESHOLD;
    startDate.setDate(startDate.getDate() - this.BUILDS_PER_DAY_TIME_RANGE + 1);
    const successBuilds = result.filter(build => this.checkBuildAfterDate(build, startDate)
      && this.checkBuildStatus(build, 'Success'));
    const averagedData = this.getAveragesByThreshold(successBuilds, startDate, threshold);
    const thresholdLine = this.getConstantLineSeries(startDate, threshold);
    this.charts[2].data[0] = averagedData.series;
    this.charts[2].colorScheme.domain = averagedData.colors;
    this.charts[2].data[1][0].series = thresholdLine;
  }

  private getAveragesByThreshold(builds: IBuild[], startDate: Date, threshold: number): any {
    const dataBucket = {};
    const date = new Date(startDate.getTime());
    for (let i = 0; i < this.BUILDS_PER_DAY_TIME_RANGE; i++) {
      dataBucket[this.toMidnight(date).getTime()] = [];
      date.setDate(date.getDate() + 1);
    }

    // Group by build time
    builds.forEach(build => {
      const index = this.toMidnight(new Date(build.endTime)).getTime();
      dataBucket[index].push(build.duration);
    });

    return this.getAveragesSeries(dataBucket, threshold);
  }

  private getAveragesSeries(dataBucket: any, threshold: number): any {
    const series = [];
    const colors = [];
    for (const key of Object.keys(dataBucket)) {
      const data = dataBucket[key];
      let value = 0;
      if (data && data.length) {
        value = data.reduce((a: number, b: number) => {
          return a + b;
        }) / data.length;
      }
      series.push(
        {
          name: new Date(+key),
          value
        }
      );
      if (value > threshold) {
        colors.push('red');
      } else {
        colors.push('green');
      }
    }
    return { series, colors };
  }

  private getConstantLineSeries(startDate: Date, threshold: number): any {
    const date = new Date(startDate.getTime());
    const series = [];
    for (let i = 0; i < this.BUILDS_PER_DAY_TIME_RANGE; i++) {
      series.push({
        name: new Date(date.getTime()),
        value: threshold
      });
      date.setDate(date.getDate() + 1);
    }
    return series;
  }

  //// *********************** HELPER UTILS *********************


  private toMidnight(date: Date): Date {
    date.setHours(0, 0, 0, 0);
    return date;
  }

  private checkBuildAfterDate(build: IBuild, date: Date): boolean {
    return build.endTime >= date.getTime();
  }

  private checkBuildStatus(build: IBuild, status: string): boolean {
    return build.buildStatus === status;
  }
}


