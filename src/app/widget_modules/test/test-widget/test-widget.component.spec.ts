import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { TestWidgetComponent } from './test-widget.component';
import { ITest, TestType } from '../interfaces';
import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SharedModule } from 'src/app/shared/shared.module';
import { CommonModule } from '@angular/common';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgbModule, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { RouterModule } from '@angular/router';
import { TestService } from '../test.service';
import { DashboardService } from 'src/app/shared/dashboard.service';
import {TestModule} from '../test.module';

class MockTestService {
  mockTestData = {
    result: [
      {
        collectorItemId: 'testCollItemId',
        timestamp: 1234,
        type: TestType.Performance,
        description: 'Success',
        result: 'Success',
        executionId: '1111',
        duration: 100,
        totalCount: 1,
        successCount: 1,
        failureCount: 0,
        startTime: 1547880494000,
        endTime: 1547880495000,
        url: 'testUrl',
        testCapabilities: [],
      }
    ]
  };

  fetchTestResults(): Observable<ITest[]> {
    return of(this.mockTestData.result);
  }
}

@NgModule({
  declarations: [],
  imports: [HttpClientTestingModule, SharedModule, CommonModule, BrowserAnimationsModule,
    RouterModule.forRoot([]), NgbModule, TestModule],
  entryComponents: []
})
class TestsModule { }


describe('TestWidgetComponent', () => {
  let component: TestWidgetComponent;
  let fixture: ComponentFixture<TestWidgetComponent>;
  let testService: TestService;
  let modalService: NgbModal;
  let dashboardService: DashboardService;
  let testResultData: ITest[];
  let testCollectorItem: any;

  const mockTest: ITest = {
    collectorItemId: 'testCollItemId',
    timestamp: 1234,
    type: TestType.Performance,
    description: 'Success',
    result: 'Success',
    executionId: '1111',
    duration: 100,
    totalCount: 1,
    successCount: 1,
    failureCount: 0,
    startTime: 1547880494000,
    endTime: 1547880495000,
    url: 'testUrl',
    testCapabilities: [],
  };

  const mockDashboard = {
    title: 'dashboard1',
    application: {
      components: [{
        collectorItems: {
          Test: [{
            id : 'id123',
            description : 'Testtool : 123',
            niceName : 'Testtool',
            enabled : true,
            errors : [],
            pushed : true,
            collectorId : '1234',
            lastUpdated : 1586901809977,
            options : {
              jobName : 'testpipeline1234',
              instanceUrl : 'url',
              testType : TestType.Functional,
            }
          }]
        }
      }]
    }
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: TestService, useClass: MockTestService },
      ],
      imports: [
        TestsModule, HttpClientTestingModule, SharedModule, CommonModule, BrowserAnimationsModule, RouterModule.forRoot([])
      ],
      declarations: [],
      schemas: [NO_ERRORS_SCHEMA]
    })
      .compileComponents();

    testResultData = [{
      result: {},
      description: 'description',
      timestamp: 1,
      executionId: '1',
      duration: 1,
      totalCount: 2,
      successCount: 2,
      failureCount: 0,
      startTime: 0,
      endTime: 1,
      url: 'url',
      type: TestType.Functional,
      collectorItemId: 'id123',
      testCapabilities: [{}],
    }] as ITest[];

    testCollectorItem = {
      id : 'id123',
      description : 'Testtool : 123',
      niceName : 'Testtool',
      enabled : true,
      errors : [],
      pushed : true,
      collectorId : '1234',
      lastUpdated : 1586901809977,
      options : {
          jobName : 'testpipeline1234',
          instanceUrl : 'url',
          testType : 'Functional'
      }
    };
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestWidgetComponent);
    component = fixture.componentInstance;
    testService = TestBed.get(TestService);
    dashboardService = TestBed.get(DashboardService);
    modalService = TestBed.get(NgbModal);
    fixture.detectChanges();
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(testService).toBeTruthy();
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
  });

  it('should call ngAfterViewInit', () => {
    component.ngAfterViewInit();
  });

  it('should generate chart', () => {
    dashboardService.dashboardConfig$ = of(mockDashboard);
    component.generateTestChart(testResultData);
    expect(component.charts[0]).toBeTruthy();
  });

  it('should generate chart item', () => {
    const clickItem = component.generateTestClickListChartItem(testResultData, 'title');
    expect(clickItem.title).toBe('title');
    expect(clickItem.subtitles[0]).toBe('100%');

    const clickItemNoData = component.generateTestClickListChartItem([], 'title');
    expect(clickItemNoData.title).toBe(component.formatTitle('title', 100));
    expect(clickItemNoData.subtitles[0]).toBe('No data found');
  });

  it('should format title correctly', () => {
    expect(component.formatTitle('123456', 6)).toBe('123...');
    expect(component.formatTitle('123456789', 8)).toBe('12345...');
  });

  it('should hit stopRefreshInterval', () => {
    component.stopRefreshInterval();
  });

  it('should assign default if no data', () => {
    component.hasData = false;
    component.setDefaultIfNoData();
    expect(component.charts[0].data.items[0].title).toEqual('No Data Found');
  });

  it('should hit startRefreshInterval', () => {
    const mockConfig = {
      name: 'codeanalysis',
      options: {
        id: 'codeanalysis0',
      },
      componentId: '1234',
      collectorItemId: '5678'
    };

    spyOn(component, 'getCurrentWidgetConfig').and.returnValues(of(mockConfig), of(mockConfig), of(null));
    spyOn(testService, 'fetchTestResults').and.returnValues(of([mockTest]), of([]));
    spyOn(dashboardService, 'checkCollectorItemTypeExist').and.returnValues(true, false);
    component.startRefreshInterval();
  });

  it('should loadCharts', () => {
    component.loadCharts([mockTest]);
  });

});
