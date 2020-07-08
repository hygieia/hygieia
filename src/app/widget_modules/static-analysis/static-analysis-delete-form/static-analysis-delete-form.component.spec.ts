import {HttpClientTestingModule} from '@angular/common/http/testing';
import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import {NgbActiveModal, NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { SharedModule } from 'src/app/shared/shared.module';
import { StaticAnalysisDeleteFormComponent } from './static-analysis-delete-form.component';
import {DashboardService} from '../../../shared/dashboard.service';
import {CollectorService} from '../../../shared/collector.service';
import {Observable, of} from 'rxjs';
import {StaticAnalysisModule} from '../static-analysis.module';

class MockCollectorService {
  mockCollectorData = {
    id: '4321',
    description: 'sonar : analysis1',
    collectorId: '1234',
    collector: {
      id: '1234',
      name: 'sonar',
      collectorType: 'CodeQuality'
    }
  };

  getItemsById(id: string): Observable<any> {
    return of(this.mockCollectorData);
  }
}

class MockDashboardService {
  mockDashboard = {
    title: 'dashboard1',
    application: {
      components: [{
        collectorItems: {
          CodeQuality: [{
            id: '1234',
            description: 'sonar : analysis1'
          }]
        }
      }]
    }
  };
  dashboardConfig$ = of(this.mockDashboard);
}

describe('StaticAnalysisDeleteFormComponent', () => {
  let component: StaticAnalysisDeleteFormComponent;
  let fixture: ComponentFixture<StaticAnalysisDeleteFormComponent>;
  let dashboardService: DashboardService;
  let collectorService: CollectorService;
  let modalService: NgbModule;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [StaticAnalysisModule, ReactiveFormsModule, NgbModule, SharedModule, HttpClientTestingModule],
      declarations: [ ],
      providers: [{ provide: NgbActiveModal, useClass: NgbActiveModal },
        { provide: CollectorService, useClass: MockCollectorService},
        { provide: DashboardService, useClass: MockDashboardService}]
    })
      .compileComponents();

  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StaticAnalysisDeleteFormComponent);
    component = fixture.componentInstance;
    dashboardService = TestBed.get(DashboardService);
    collectorService = TestBed.get(CollectorService);
    modalService = TestBed.get(NgbModal);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
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
    expect(component.staticAnalysisDeleteForm).toBeDefined();
  });

  it('should assign selected job after submit', () => {
    component.createDeleteForm();
    expect(component.staticAnalysisDeleteForm.get('staticAnalysisJob').value).toEqual('');
    component.staticAnalysisDeleteForm = component.formBuilder.group({staticAnalysisJob: 'sonarJob1'});
    component.submitDeleteForm();
    expect(component.staticAnalysisDeleteForm.get('staticAnalysisJob').value).toEqual('sonarJob1');
  });

  it('should load saved static analysis job', () => {
    component.getSavedCodeQualityJob();
    collectorService.getItemsById('4321').subscribe(result => {
      expect(component.staticAnalysisDeleteForm.get('staticAnalysisJob').value).toEqual(result);
    });
  });

});
