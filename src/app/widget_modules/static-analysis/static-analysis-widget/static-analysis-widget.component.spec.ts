import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { NgbModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { DashboardService } from 'src/app/shared/dashboard.service';
import { SharedModule } from 'src/app/shared/shared.module';

import { StaticAnalysisService } from '../static-analysis.service';
import { IStaticAnalysis } from '../interfaces';
import { StaticAnalysisWidgetComponent} from './static-analysis-widget.component';

class MockStaticAnalysisService {

  mockStaticAnalysisData = {
    result: [
      {
        id: '123',
        collectorItemId: '123',
        timestamp: 1552590574305,
        name: 'sonar-project-1',
        url: 'https://sonar.com',
        version: '0.0.1',
        metrics: [],
      }
    ],
    lastUpdated: 1553613455230
  };

  fetchDetails(): Observable<IStaticAnalysis[]> {
    return of(this.mockStaticAnalysisData.result);
  }
}

@NgModule({
  declarations: [],
  imports: [HttpClientTestingModule, SharedModule, CommonModule, BrowserAnimationsModule, RouterModule.forRoot([]), NgbModule],
  entryComponents: []
})
class TestModule { }

describe('StaticAnalysisWidgetComponent', () => {
  let component: StaticAnalysisWidgetComponent;
  let staticAnalysisService: StaticAnalysisService;
  let dashboardService: DashboardService;
  let modalService: NgbModal;
  let fixture: ComponentFixture<StaticAnalysisWidgetComponent>;
  let staticAnalysisTestData: IStaticAnalysis;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: StaticAnalysisService, useClass: MockStaticAnalysisService },
      ],
      imports: [
        TestModule, HttpClientTestingModule, SharedModule, CommonModule, BrowserAnimationsModule, RouterModule.forRoot([])
      ],
      declarations: [],
      schemas: [NO_ERRORS_SCHEMA]
    })
      .compileComponents();

    fixture = TestBed.createComponent(StaticAnalysisWidgetComponent);
    component = fixture.componentInstance;
    staticAnalysisService = TestBed.get(StaticAnalysisService);
    dashboardService = TestBed.get(DashboardService);
    modalService = TestBed.get(NgbModal);

    staticAnalysisTestData = {
      id: '123',
      collectorItemId: '123',
      timestamp: 1552590574305,
      name: 'sonar-project-1',
      url: 'https://sonar.com',
      version: '0.0.1',
      metrics: [
        {
          name: component.staticAnalysisMetrics.blockerViolations,
          value: '1',
          formattedValue: '1',
        },
        {
          name: component.staticAnalysisMetrics.criticalViolations,
          value: '1',
          formattedValue: '1',
        },
        {
          name: component.staticAnalysisMetrics.majorViolations,
          value: '1',
          formattedValue: '1',
        },
        {
          name: component.staticAnalysisMetrics.totalIssues,
          value: '3',
          formattedValue: '3',
        },
        {
          name: component.staticAnalysisMetrics.codeCoverage,
          value: '55.5',
          formattedValue: '55.5%',
        },
        {
          name : component.staticAnalysisMetrics.numCodeLines,
          value : '123',
          formattedValue : '123',
        },
        {
          name : component.staticAnalysisMetrics.alertStatus,
          value : 'OK',
          formattedValue : 'OK',
        },
        {
          name : component.staticAnalysisMetrics.techDebt,
          value : '60',
          formattedValue : '60min'
        },
        {
          name : component.staticAnalysisMetrics.totalTests,
          value : '10',
          formattedValue : '10'
        },
        {
          name : component.staticAnalysisMetrics.testSuccesses,
          value : '10',
          formattedValue : '10'
        },
        {
          name : component.staticAnalysisMetrics.testFailures,
          value : '0',
          formattedValue : '0'
        },
        {
          name : component.staticAnalysisMetrics.testErrors,
          value : '0',
          formattedValue : '0'
        },
      ],
    } as IStaticAnalysis;
  }));

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(staticAnalysisService).toBeTruthy();
    expect(dashboardService).toBeTruthy();
    expect(modalService).toBeTruthy();
    expect(fixture).toBeTruthy();
  });

  it('should call ngOnInit()', () => {
    component.ngOnInit();
  });

  it('should set initial value of widgetId, layout, and charts', () => {
    component.ngOnInit();
    expect(component.widgetId).toBeDefined();
    expect(component.layout).toBeDefined();
    expect(component.charts).toBeDefined();
  });

  it('should set interval refresh subscription', () => {
    component.ngOnInit();
    expect(component.staticAnalysisMetrics).toBeDefined();
    expect(component.qualityGateStatuses).toBeDefined();
  });

  it('should call ngOnDestroy', () => {
    component.ngOnDestroy();
  });

  it('should hit stopRefreshInterval', () => {
    component.stopRefreshInterval();
  });

  it('should call ngAfterViewInit', () => {
    component.ngAfterViewInit();
  });

  it('should hit startRefreshInterval', () => {
    component.startRefreshInterval();
  });

  it('should loadEmptyChart', () => {
    component.loadEmptyChart();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StaticAnalysisWidgetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should generateProjectDetails', () => {
    component.generateProjectDetails(staticAnalysisTestData);
    expect(component.charts[0].data.items.length).toEqual(4);
    expect(component.charts[0].data.url).toEqual('https://sonar.com');
    expect(component.charts[0].data.version).toEqual('0.0.1');
    expect(component.charts[0].data.name).toEqual('sonar-project-1');
    expect(component.charts[0].data.timestamp).toEqual(new Date(1552590574305));

    // data is null
    component.generateProjectDetails(null);
  });

  it('should generateViolations', () => {
    component.generateViolations(staticAnalysisTestData);
    expect(component.charts[1].data[0].value).toEqual('1');
    expect(component.charts[1].data[1].value).toEqual('1');
    expect(component.charts[1].data[2].value).toEqual('1');
    expect(component.charts[1].data[3].value).toEqual('3');

    // data is null
    component.generateViolations(null);
  });

  it('should generateCoverage', () => {
    component.generateCoverage(staticAnalysisTestData);
    expect(component.charts[2].data.dataPoints[0].value).toEqual(55.5);
    expect(component.charts[2].data.units).toEqual('123 lines of code');

    // data is null
    component.generateCoverage(null);
  });

  it('should generateUnitTestMetrics', () => {
    component.generateUnitTestMetrics(staticAnalysisTestData);
    expect(component.charts[3].data.items.length).toEqual(4);

    // data is null
    component.generateUnitTestMetrics(null);
  });

});


