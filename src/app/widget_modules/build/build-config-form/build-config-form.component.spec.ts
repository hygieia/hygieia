import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NgbActiveModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SharedModule } from 'src/app/shared/shared.module';

import { BuildConfigFormComponent } from './build-config-form.component';
import {DashboardService} from '../../../shared/dashboard.service';

describe('BuildConfigFormComponent', () => {
  let component: BuildConfigFormComponent;
  let fixture: ComponentFixture<BuildConfigFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, NgbModule, SharedModule, HttpClientTestingModule],
      declarations: [ ],
      providers: [NgbActiveModal]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BuildConfigFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set widgetConfig', () => {
    const widgetConfigData = {
      options: {
        id: 1232,
        buildDurationThreshold: '',
        consecutiveFailureThreshold: '',
      }
    };
    component.widgetConfig = widgetConfigData;
  });

  it('should call ngOnInit()', () => {
    component.ngOnInit();
  });
});
