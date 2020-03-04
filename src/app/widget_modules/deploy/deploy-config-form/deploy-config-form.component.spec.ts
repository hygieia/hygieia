import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NgbActiveModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SharedModule } from 'src/app/shared/shared.module';
import { DeployConfigFormComponent } from './deploy-config-form.component';

describe('DeployConfigFormComponent', () => {
  let component: DeployConfigFormComponent;
  let fixture: ComponentFixture<DeployConfigFormComponent>;

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
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should pass through getDeploymentJobs, getDashboardComponent, and loadSavedDeployment', () => {
    component.getDeploymentJobs(null);
    component.ngOnInit();
  });
});
