import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import {NgbActiveModal, NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { SharedModule } from 'src/app/shared/shared.module';
import { DeployConfigFormComponent } from './deploy-config-form.component';
import {AuthService} from '../../../core/services/auth.service';
import {Observable, of} from 'rxjs';
import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {RouterModule} from '@angular/router';
import {DashboardService} from '../../../shared/dashboard.service';
import {CollectorService} from '../../../shared/collector.service';
import {DeployModule} from '../deploy.module';

class MockCollectorService {
  mockCollectorData = {
    id: '4321',
    description: 'Deploy : job1',
    collectorId: '1234',
    collector: {
      id: '1234',
      name: 'Deploy',
      collectorType: 'Deploy'
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
          Deployment: [{
            id: '1234',
            description: 'job1',
            niceName: 'SomeDeployment'
          }]
        }
      }]
    }
  };
  dashboardConfig$ = of(this.mockDashboard);
}

@NgModule({
  declarations: [],
  imports: [DeployModule, HttpClientTestingModule, SharedModule, CommonModule, BrowserAnimationsModule,
    RouterModule.forRoot([]), NgbModule],
  entryComponents: []
})
class TestModule { }

describe('DeployConfigFormComponent', () => {
  let component: DeployConfigFormComponent;
  let fixture: ComponentFixture<DeployConfigFormComponent>;
  let httpMock: HttpTestingController;
  let service: AuthService;
  let dashboardService: DashboardService;
  let collectorService: CollectorService;
  let modalService: NgbModule;

  const deployCollectorItem = {
    id: '1234',
    description: 'job1',
    niceName: 'SomeDeployment'
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [TestModule, ReactiveFormsModule, NgbModule, SharedModule, HttpClientTestingModule],
      providers: [
        { provide: NgbActiveModal, useClass: NgbActiveModal },
        { provide: CollectorService, useClass: MockCollectorService},
        { provide: DashboardService, useClass: MockDashboardService}]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeployConfigFormComponent);
    component = fixture.componentInstance;
    dashboardService = TestBed.get(DashboardService);
    collectorService = TestBed.get(CollectorService);
    modalService = TestBed.get(NgbModal);
    fixture.detectChanges();
    httpMock = TestBed.get(HttpTestingController);
    service = TestBed.get(AuthService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should pass through getDeploymentJobs, getDashboardComponent, and loadSavedDeployment', () => {
    component.ngOnInit();
  });

  it('should set widgetConfig', () => {
    let widgetConfigData = {
      options: {
        id: 1232,
        deployRegex: [''],
        deployAggregateServer: true,
      }
    };
    component.widgetConfig = widgetConfigData;

    const widgetConfigDataNoAggregate = {
      options: {
        id: 1232,
        deployRegex: [''],
        deployAggregateServer: false,
      }
    };
    component.widgetConfig = widgetConfigDataNoAggregate;

    widgetConfigData = null;
    component.widgetConfig = widgetConfigData;
  });

  it('should get deploy job()', () => {
    const title = component.getDeployJob(deployCollectorItem);
    expect(title).toBeTruthy();

    const noTitle = component.getDeployJob(null);
    expect(noTitle).toBeFalsy();
  });

  it('should assign selected job after submit', () => {
    component.createForm();
    expect(component.deployConfigForm.get('deployJob').value).toEqual('');
    component.deployConfigForm = component.formBuilder.group({deployJob: 'job1'});
    component.submitForm();
    expect(component.deployConfigForm.get('deployJob').value).toEqual('job1');
  });

  it('should load saved deploy job', () => {
    component.loadSavedDeployJobs();
    collectorService.getItemsById('4321').subscribe(result => {
      expect(component.deployConfigForm.get('deployJob').value).toEqual(result);
    });
  });
});
