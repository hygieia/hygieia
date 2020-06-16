import {
  Component,
  OnInit,
  ComponentFactoryResolver,
  ChangeDetectorRef,
  AfterViewInit,
  ViewChild,
  OnDestroy
} from '@angular/core';
import { WidgetComponent } from 'src/app/shared/widget/widget.component';
import { OneByTwoLayoutComponent } from 'src/app/shared/layouts/one-by-two-layout/one-by-two-layout.component';
import { TestService } from '../test.service';
import { DashboardService } from 'src/app/shared/dashboard.service';
import { ActivatedRoute } from '@angular/router';
import { TEST_CHARTS } from './test-charts';
import {
  startWith,
  distinctUntilChanged,
  switchMap,
  map,
  take
} from 'rxjs/operators';
import { LayoutDirective } from 'src/app/shared/layouts/layout.directive';
import { Subscription, of } from 'rxjs';
import { ITest, TestType } from '../interfaces';
import { IClickListItemTest, IClickListData } from 'src/app/shared/charts/click-list/click-list-interfaces';
import { TestDetailComponent } from '../test-detail/test-detail.component';
import {WidgetState} from '../../../shared/widget-header/widget-state';

@Component({
  selector: 'app-test-widget',
  templateUrl: './test-widget.component.html',
  styleUrls: ['./test-widget.component.sass']
})
export class TestWidgetComponent extends WidgetComponent implements OnInit, AfterViewInit, OnDestroy {

   // Reference to the subscription used to refresh the widget
  private intervalRefreshSubscription: Subscription;

  private readonly TEST_TIME_RANGE = 999 * 24 * 60 * 60 * 1000;

  @ViewChild(LayoutDirective, {static: false}) childLayoutTag: LayoutDirective;

  constructor(componentFactoryResolver: ComponentFactoryResolver,
              cdr: ChangeDetectorRef,
              dashboardService: DashboardService,
              route: ActivatedRoute,
              private testService: TestService) {
    super(componentFactoryResolver, cdr, dashboardService, route);
  }

  ngOnInit() {
    this.widgetId = 'codeanalysis0';
    this.layout = OneByTwoLayoutComponent;
    this.charts = TEST_CHARTS;
    this.auditType = ['TEST_RESULT', 'PERF_TEST'];
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
      startWith(-1), // Refresh this widget seperate from dashboard (ex. config is updated)
      distinctUntilChanged(), // If dashboard is loaded the first time, ignore widget double refresh
      switchMap( _ => this.getCurrentWidgetConfig()),
      switchMap( widgetConfig => {
        if (!widgetConfig) {
          this.widgetConfigExists = false;
          return of([]);
        }
        this.widgetConfigExists = true;
        // check if collector item type is tied to dashboard
        // if true, set state to READY, otherwise keep at default CONFIGURE
        if (this.dashboardService.checkCollectorItemTypeExist('Test')) {
          this.state = WidgetState.READY;
        }
        const currentTime: number = new Date().getTime();
        const tests$ = this.testService.fetchTestResults(widgetConfig.componentId, currentTime - this.TEST_TIME_RANGE,
          currentTime, 4, [TestType.Functional, TestType.Performance]);
        return tests$;
      })).subscribe( tests => {
        if (this.hasData) {
          this.loadCharts(tests);
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

  stopRefreshInterval() {
    if (this.intervalRefreshSubscription) {
      this.intervalRefreshSubscription.unsubscribe();
    }
  }

  loadCharts(tests: ITest[]) {
    this.generateTestChart(tests);
    super.loadComponent(this.childLayoutTag);
  }

  // ************************* Generate chart *************************
  generateTestChart(tests: ITest[]) {
    const chartItems = {};
    // Generate chart item for each TEST collector item
    this.dashboardService.dashboardConfig$.pipe(take(1)).subscribe(dashboard => {
      const testCollectorItems = dashboard.application.components[0].collectorItems.Test;
      if (!testCollectorItems) { return; }
      for (const testCollectorItem of testCollectorItems) {
        const type = testCollectorItem.options.testType;
        const tmp = tests.filter(test => test.collectorItemId === testCollectorItem.id);
        const chartItem = this.generateTestClickListChartItem(tmp, testCollectorItem.description);
        if (chartItems[type] === undefined) {
          chartItems[type] = [];
        }
        chartItems[type].push(chartItem);
      }
    });
    // Load chart items into charts
    this.charts[0].data = {
      items: chartItems[TestType.Functional],
      clickableContent: TestDetailComponent
    } as IClickListData;
    this.charts[1].data = {
      items: chartItems[TestType.Performance],
      clickableContent: TestDetailComponent
    } as IClickListData;
  }

  // ************************* Generate individual chart item *************************
  generateTestClickListChartItem(tests: ITest[], title: string): IClickListItemTest {
    if (tests === undefined || tests.length === 0) {
      return {
        title: this.formatTitle(title, 100),
        subtitles: [
          'No data found.',
          ''
        ]
      } as IClickListItemTest;
    }
    const test = tests[0];
    const successRate = ((test.successCount / test.totalCount) * 100).toFixed(0) + '%';
    return {
      title: this.formatTitle(title, 100),
      subtitles: [
        successRate,
        new Date(test.endTime),
      ],
      data: test
    } as IClickListItemTest;
  }

  // ************************* HELPER FUNCTIONS *************************
  formatTitle(title: string, length: number): string {
    const fit = title.length < length;
    return fit ? title : title.slice(0, length - 3) + '...';
  }

  setDefaultIfNoData() {
    if (!this.hasData) {
      this.charts[0].data = { items: [{ title: 'No Data Found' }]};
      this.charts[1].data = { items: [{ title: 'No Data Found' }]};
    }
    super.loadComponent(this.childLayoutTag);
  }

}


