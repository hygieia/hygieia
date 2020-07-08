import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import {NgbActiveModal, NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { SharedModule } from 'src/app/shared/shared.module';
import { FeatureDeleteFormComponent } from './feature-delete-form.component';
import {Observable, of} from 'rxjs';
import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {RouterModule} from '@angular/router';
import {DashboardService} from '../../../shared/dashboard.service';
import {CollectorService} from '../../../shared/collector.service';
import {FeatureModule} from '../feature.module';

class MockCollectorService {
  mockCollectorData = {
    id: '4321',
    collectorId: '1234',
    options : {
      teamName : 'team',
      featureTool : 'feature',
      projectName : 'someProject',
      projectId : '5678',
      teamId : '1111'
    }
  };
  mockCollectorType = [{name: 'test', collectorType: 'AgileTool'}];

  getItemsById(id: string): Observable<any> {
    return of(this.mockCollectorData);
  }

  collectorsByType(collectorType): Observable<any> {
    return of(this.mockCollectorType);
  }
}

class MockDashboardService {
  mockDashboard = {
    title: 'dashboard1',
    application: {
      components: [{
        collectorItems: {
          AgileTool: [{
            id: '1234',
            options : {
              teamName : 'team',
              featureTool : 'feature',
              projectName : 'someProject',
              projectId : '5678',
              teamId : '1111'
            }
          }]
        }
      }]
    }
  };
  dashboardConfig$ = of(this.mockDashboard);
}

@NgModule({
  declarations: [],
  imports: [HttpClientTestingModule, SharedModule, CommonModule, BrowserAnimationsModule,
    RouterModule.forRoot([]), NgbModule, FeatureModule],
  entryComponents: []
})
class TestModule { }

describe('FeatureDeleteFormComponent', () => {
  let component: FeatureDeleteFormComponent;
  let fixture: ComponentFixture<FeatureDeleteFormComponent>;
  let dashboardService: DashboardService;
  let collectorService: CollectorService;
  let modalService: NgbModule;

  const featureCollectorItem = {
    id: '1234',
    options : {
      teamName : 'team',
      featureTool : 'feature',
      projectName : 'someProject',
      projectId : '5678',
      teamId : '1111'
    }
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [TestModule, ReactiveFormsModule, NgbModule, SharedModule, HttpClientTestingModule],
      declarations: [ ],
      providers: [
        { provide: NgbActiveModal, useClass: NgbActiveModal },
        { provide: CollectorService, useClass: MockCollectorService},
        { provide: DashboardService, useClass: MockDashboardService}]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FeatureDeleteFormComponent);
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
        featureTool: 'featureTool',
        sprintType: 'sprint',
        listType: 'listType',
      }
    };
    component.widgetConfig = widgetConfigData;
    component.widgetConfig = null;
  });

  it('should call ngOnInit()', () => {
    component.ngOnInit();
  });

  it('should assign selected feature after submit', () => {
    component.teamId = featureCollectorItem.options.teamId;
    component.projectId = featureCollectorItem.options.projectId;
    component.createDeleteForm();
    expect(component.featureDeleteForm.get('featureTool').value).toEqual('');
    expect(component.featureDeleteForm.get('projectName').value).toEqual('');
    expect(component.featureDeleteForm.get('teamName').value).toEqual('');
    expect(component.featureDeleteForm.get('sprintType').value).toEqual('');
    expect(component.featureDeleteForm.get('listType').value).toEqual('');

    component.featureDeleteForm = component.formBuilder.group({
      featureTool: 'feature',
      projectName: {
        options: {
          projectName: 'project'
        }
      },
      teamName: {
        options: {
          teamName: 'team'
        }
      },
      sprintType: 'sprint',
      listType: 'list',

    });
    component.submitDeleteForm();
    expect(component.featureDeleteForm.get('featureTool').value).toEqual('feature');
    expect(component.featureDeleteForm.get('projectName').value.options.projectName).toEqual('project');
    expect(component.featureDeleteForm.get('teamName').value.options.teamName).toEqual('team');
    expect(component.featureDeleteForm.get('sprintType').value).toEqual('sprint');
    expect(component.featureDeleteForm.get('listType').value).toEqual('list');

  });

  it('should get saved features', () => {
    component.getSavedFeatures();
    collectorService.getItemsById('4321').subscribe(result => {
      expect(component.featureDeleteForm.get('projectName').value).toEqual(result);
      expect(component.featureDeleteForm.get('teamName').value).toEqual(result);
    });
  });
});
