import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {async, ComponentFixture, inject, TestBed} from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NgbActiveModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SharedModule } from 'src/app/shared/shared.module';
import { StaticAnalysisConfigFormComponent } from './static-analysis-config-form.component';
import {DashboardService} from '../../../shared/dashboard.service';
import {GET_DASHBOARD_MOCK} from '../../../shared/dashboard.service.mockdata';

describe('StaticAnalysisConfigFormComponent', () => {
  let component: StaticAnalysisConfigFormComponent;
  let fixture: ComponentFixture<StaticAnalysisConfigFormComponent>;
  let dashboardService: DashboardService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, NgbModule, SharedModule, HttpClientTestingModule],
      declarations: [ ],
      providers: [NgbActiveModal]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StaticAnalysisConfigFormComponent);
    component = fixture.componentInstance;
    dashboardService = TestBed.get(DashboardService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should getStaticAnalysisTitle', () => {
    const collectorItem = {
      description : 'example-repo',
      niceName : 'example',
    };
    expect(component.getStaticAnalysisTitle(collectorItem)).toEqual('example : example-repo');
    expect(component.getStaticAnalysisTitle(null)).toEqual('');
  });

  it('should set widgetConfig', () => {
    const widgetConfigData = {
      options: {
        id: 123,
      }
    };
    component.widgetConfig = widgetConfigData;
    component.widgetConfig = null;
  });

  it('should call ngOnInit()', () => {
    component.ngOnInit();
  });

  it('should have an initial static config form', () => {
    const widgetConfigData = {
      options: {
        id: 123,
      }
    };
    component.widgetConfig = widgetConfigData;
    expect(component.staticAnalysisConfigForm).toBeDefined();
  });

  it('should find and load saved code quality job', () => {
    // Load initial dashboard
    inject([HttpTestingController, DashboardService],
      (httpMock: HttpTestingController, service: DashboardService) => {
        service.loadDashboard('123');
        service.dashboardConfig$.subscribe(dashboard => {
          expect(dashboard).toBeTruthy();
        });

        const request = httpMock.expectOne(req => req.method === 'GET');
        request.flush(GET_DASHBOARD_MOCK);

        component.ngOnInit();
      });
  });

  it('should not find or load saved code quality job', () => {
    inject([HttpTestingController, DashboardService],
      (httpMock: HttpTestingController, service: DashboardService) => {
        service.loadDashboard('123');
        service.dashboardConfig$.subscribe(dashboard => {
          expect(dashboard).toBeTruthy();
        });

        const mock = GET_DASHBOARD_MOCK;
        mock.application.components[0].collectorItems.CodeQuality = null;
        const request = httpMock.expectOne(req => req.method === 'GET');
        request.flush(mock);

        component.ngOnInit();
      });
  });

});
