import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NgbActiveModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SharedModule } from 'src/app/shared/shared.module';
import { DeployConfigFormComponent } from './deploy-config-form.component';
import {AuthService} from '../../../core/services/auth.service';

class DevExTokenResponse {
}

describe('DeployConfigFormComponent', () => {
  let component: DeployConfigFormComponent;
  let fixture: ComponentFixture<DeployConfigFormComponent>;
  let httpMock: HttpTestingController;
  let service: AuthService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, NgbModule, SharedModule, HttpClientTestingModule],
      providers: [NgbActiveModal]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeployConfigFormComponent);
    component = fixture.componentInstance;
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
    const widgetConfigData = {
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
  });
});
