import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import {NgbActiveModal, NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { SharedModule } from 'src/app/shared/shared.module';

import { BuildConfigFormComponent } from './build-config-form.component';
import {Observable, of} from 'rxjs';
import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {RouterModule} from '@angular/router';
import {DashboardService} from '../../../shared/dashboard.service';
import {CollectorService} from '../../../shared/collector.service';
import {BuildModule} from '../build.module';

class MockCollectorService {
  mockCollectorData = {
    id: '4321',
    description: 'Build : job1',
    collectorId: '1234',
    collector: {
      id: '1234',
      name: 'Build',
      collectorType: 'Build'
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
          Build: [{
            id: '1234',
            description: 'job1',
            niceName: 'SomeBuild'
          }]
        }
      }]
    }
  };
  dashboardConfig$ = of(this.mockDashboard);
}

@NgModule({
  declarations: [],
  imports: [BuildModule, HttpClientTestingModule, SharedModule, CommonModule, BrowserAnimationsModule, RouterModule.forRoot([]), NgbModule],
  entryComponents: []
})
class TestModule { }

describe('BuildConfigFormComponent', () => {
  let component: BuildConfigFormComponent;
  let fixture: ComponentFixture<BuildConfigFormComponent>;
  let dashboardService: DashboardService;
  let collectorService: CollectorService;
  let modalService: NgbModule;

  const buildCollectorItem = {
    id: '1234',
    description: 'job1',
    niceName: 'SomeBuild'
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, NgbModule, HttpClientTestingModule, TestModule],
      declarations: [ ],
      providers: [
        { provide: NgbActiveModal, useClass: NgbActiveModal },
        { provide: CollectorService, useClass: MockCollectorService},
        { provide: DashboardService, useClass: MockDashboardService}]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BuildConfigFormComponent);
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
    let widgetConfigData = {
      options: {
        id: 1232,
        buildDurationThreshold: '',
        consecutiveFailureThreshold: '',
      }
    };
    component.widgetConfig = widgetConfigData;

    widgetConfigData = null;
    component.widgetConfig = widgetConfigData;
  });

  it('should call ngOnInit()', () => {
    component.ngOnInit();
  });

  it('should get build title()', () => {
    const title = component.getBuildTitle(buildCollectorItem);
    expect(title).toBeTruthy();

    const noTitle = component.getBuildTitle(null);
    expect(noTitle).toBeFalsy();
  });

  it('should assign selected job after submit', () => {
    component.createForm();
    expect(component.buildConfigForm.get('buildJob').value).toEqual('');
    component.buildConfigForm = component.formBuilder.group({buildJob: 'job1'});
    component.submitForm();
    expect(component.buildConfigForm.get('buildJob').value).toEqual('job1');
  });

  it('should load saved build job', () => {
    component.loadSavedBuildJob();
    collectorService.getItemsById('4321').subscribe(result => {
      expect(component.buildConfigForm.get('buildJob').value).toEqual(result);
    });
  });

});
