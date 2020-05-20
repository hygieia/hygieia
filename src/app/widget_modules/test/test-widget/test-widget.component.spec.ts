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

class MockTestService {
  mockTestData = {
    results: [
    ]
  };

  fetchTestResults(): Observable<ITest[]> {
    return of(this.mockTestData.results);
  }
}

@NgModule({
  declarations: [],
  imports: [HttpClientTestingModule, SharedModule, CommonModule, BrowserAnimationsModule, RouterModule.forRoot([]), NgbModule],
  entryComponents: []
})
class TestModule { }


describe('TestWidgetComponent', () => {
  let component: TestWidgetComponent;
  let fixture: ComponentFixture<TestWidgetComponent>;
  let testService: TestService;
  let modalService: NgbModal;
  let dashboardService: DashboardService;
  let testResultData: ITest[];
  let testCollectorItem: any;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: TestService, useClass: MockTestService },
      ],
      imports: [
        TestModule, HttpClientTestingModule, SharedModule, CommonModule, BrowserAnimationsModule, RouterModule.forRoot([])
      ],
      declarations: [],
      schemas: [NO_ERRORS_SCHEMA]
    })
      .compileComponents();

    fixture = TestBed.createComponent(TestWidgetComponent);
    component = fixture.componentInstance;
    testService = TestBed.get(TestService);
    dashboardService = TestBed.get(DashboardService);
    modalService = TestBed.get(NgbModal);

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
      _id : 'id1234',
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

  it('should call ngOnDestroy', () => {
    component.ngOnDestroy();
  });

  it('should call ngAfterViewInit', () => {
    component.ngAfterViewInit();
  });

  it('should hit startRefreshInterval', () => {
    component.startRefreshInterval();
  });

  it('should generate chart', () => {
    component.generateTestChart(testResultData);
    expect(component.charts[0]).toBeTruthy();
  });

  it('should generate chart item', () => {
    const clickItem = component.generateTestClickListChartItem(testResultData, 'title');
    expect(clickItem.title).toBe('title');
    expect(clickItem.subtitles[0]).toBe('100%');
  });

  it('should format title correctly', () => {
    expect(component.formatTitle('123456', 6)).toBe('123...');
    expect(component.formatTitle('123456789', 8)).toBe('12345...');
  });

  it('should hit stopRefreshInterval', () => {
    component.stopRefreshInterval();
  });

});
