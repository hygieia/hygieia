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
import {of, Subscription} from 'rxjs';
import {distinctUntilChanged, startWith, switchMap} from 'rxjs/operators';
import {
  IClickListData,
  IClickListItem,
  IClickListItemStaticAnalysis
} from 'src/app/shared/charts/click-list/click-list-interfaces';
import {DashboardService} from 'src/app/shared/dashboard.service';
import {LayoutDirective} from 'src/app/shared/layouts/layout.directive';
import {TwoByTwoLayoutComponent} from 'src/app/shared/layouts/two-by-two-layout/two-by-two-layout.component';
import {WidgetComponent} from 'src/app/shared/widget/widget.component';
import {StaticAnalysisService} from '../static-analysis.service';
import {STATICANALYSIS_CHARTS} from './static-analysis-charts';
import {IStaticAnalysis} from '../interfaces';
import {StaticAnalysisDetailComponent} from '../static-analysis-detail/static-analysis-detail.component';
import {isUndefined} from 'util';

@Component({
  selector: 'app-static-analysis-widget',
  templateUrl: './static-analysis-widget.component.html',
  styleUrls: ['./static-analysis-widget.component.scss']
})
export class StaticAnalysisWidgetComponent extends WidgetComponent implements OnInit, AfterViewInit, OnDestroy {

  // Code Quality Metric Field Names
  public readonly staticAnalysisMetrics = {
    // quality gate
    qualityGateDetails: 'quality_gate_details',
    alertStatus: 'alert_status',
    techDebt: 'sqale_index',
    // violations
    totalIssues: 'violations',
    blockerViolations: 'blocker_violations',
    criticalViolations: 'critical_violations',
    majorViolations: 'major_violations',
    // coverage
    codeCoverage: 'coverage',
    lineCoverage: 'line_coverage',
    numCodeLines: 'ncloc',
    // vulnerabilities
    newVulnerabilities: 'new_vulnerabilities',
    // unit test metrics
    testSuccesses: 'test_success_density',
    testFailures: 'test_failures',
    testErrors: 'test_errors',
    totalTests: 'tests',
  };

  // Code Quality Quality Gate Status Names
  public readonly qualityGateStatuses = {
    OK: 'OK',
    WARN: 'WARN',
    FAILED: 'ERROR',
  };

  // Reference to the subscription used to refresh the widget
  private intervalRefreshSubscription: Subscription;

  @ViewChild(LayoutDirective, {static: false}) childLayoutTag: LayoutDirective;

  constructor(componentFactoryResolver: ComponentFactoryResolver,
              cdr: ChangeDetectorRef,
              dashboardService: DashboardService,
              route: ActivatedRoute,
              private staticAnalysisService: StaticAnalysisService) {
    super(componentFactoryResolver, cdr, dashboardService, route);
  }

  // Initialize the widget and set layout and charts.
  ngOnInit() {
    this.widgetId = 'codeanalysis0';
    this.layout = TwoByTwoLayoutComponent;
    this.charts = STATICANALYSIS_CHARTS;
    this.auditType = 'CODE_QUALITY';
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
        return this.staticAnalysisService.fetchStaticAnalysis(widgetConfig.componentId, 1);
      })).subscribe(result => {
        if (result && result.length > 0) {
          this.loadCharts(result[0]);
        } else {
          // code quality item could not be found
          this.loadEmptyChart();
        }
      });
  }

  // Unsubscribe from the widget refresh observable, which stops widget updating.
  stopRefreshInterval() {
    if (this.intervalRefreshSubscription) {
      this.intervalRefreshSubscription.unsubscribe();
    }
  }

  loadCharts(result: IStaticAnalysis) {
    this.generateProjectDetails(result);
    this.generateViolations(result);
    this.generateCoverage(result);
    this.generateUnitTestMetrics(result);
    super.loadComponent(this.childLayoutTag);
  }

  loadEmptyChart() {
    super.loadComponent(this.childLayoutTag);
  }

  // *********************** DETAILS/QUALITY *********************

  generateProjectDetails(result: IStaticAnalysis) {

    if (!result) {
      return;
    }

    const latestDetails = [
      {
        status: null,
        statusText: '',
        title: 'Name',
        subtitles: [result.name],
      },
      {
        status: null,
        statusText: '',
        title: 'Version',
        subtitles: [result.version],
      },
      {
        status: null,
        statusText: '',
        title: 'Quality Gate',
        subtitles: [result.metrics.find(metric => metric.name === this.staticAnalysisMetrics.alertStatus).value],
      },
      {
        status: null,
        statusText: '',
        title: 'Technical Debt',
        subtitles: [result.metrics.find(metric => metric.name === this.staticAnalysisMetrics.techDebt).formattedValue],
      },
    ] as IClickListItem[];

    this.charts[0].data = {
      items: latestDetails,
      clickableContent: null,
      clickableHeader: StaticAnalysisDetailComponent,
      url: result.url,
      version: result.version,
      name: result.name,
      timestamp: new Date(result.timestamp),
    } as IClickListItemStaticAnalysis;

  }

  // *********************** COVERAGE (CODE) ****************************

  generateCoverage(result: IStaticAnalysis) {

    if (!result) {
      return;
    }

    const coverage = parseFloat(result.metrics.find(metric => metric.name === this.staticAnalysisMetrics.codeCoverage).value);
    const loc = parseFloat(result.metrics.find(metric => metric.name === this.staticAnalysisMetrics.numCodeLines).value);

    this.charts[1].data.results[0].value = coverage;
    this.charts[1].data.customLabelValue = loc;

  }

  // *********************** VIOLATIONS *****************************

  generateViolations(result: IStaticAnalysis) {

    if (!result) {
      return;
    }

    const blocker = parseFloat(result.metrics.find(metric => metric.name === this.staticAnalysisMetrics.blockerViolations).value);
    const critical = parseFloat(result.metrics.find(metric => metric.name === this.staticAnalysisMetrics.criticalViolations).value);
    const major = parseFloat(result.metrics.find(metric => metric.name === this.staticAnalysisMetrics.majorViolations).value);
    const total = parseFloat(result.metrics.find(metric => metric.name === this.staticAnalysisMetrics.totalIssues).value);

    this.charts[2].data[0].value = blocker;
    this.charts[2].data[1].value = critical;
    this.charts[2].data[2].value = major;
    this.charts[2].data[3].value = total;

  }

  // *********************** UNIT TEST METRICS ****************************

  generateUnitTestMetrics(result: IStaticAnalysis) {

    if (!result) {
      return;
    }

    const testSuccesses = result.metrics.find(metric => metric.name === this.staticAnalysisMetrics.testSuccesses);
    const testFailures = result.metrics.find(metric => metric.name === this.staticAnalysisMetrics.testFailures);
    const testErrors = result.metrics.find(metric => metric.name === this.staticAnalysisMetrics.testErrors);
    const totalTests = result.metrics.find(metric => metric.name === this.staticAnalysisMetrics.totalTests);

    const latestDetails = [
      {
        status: null,
        statusText: '',
        title: 'Success',
        subtitles: [isUndefined(testSuccesses) ? '' : (parseFloat(testSuccesses.value) / 100) * parseInt(totalTests.value, 10)],
      },
      {
        status: null,
        statusText: '',
        title: 'Failures',
        subtitles: [isUndefined(testFailures) ? '' : testFailures.value],
      },
      {
        status: null,
        statusText: '',
        title: 'Errors',
        subtitles: [isUndefined(testErrors) ? '' : testErrors.value],
      },
      {
        status: null,
        statusText: '',
        title: 'Total Tests',
        subtitles: [isUndefined(totalTests) ? '' : totalTests.value],
      },
    ] as IClickListItem[];

    this.charts[3].data = {
      items: latestDetails,
      clickableContent: null,
      clickableHeader: null,
    } as IClickListData;

  }

}
