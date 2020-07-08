import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { NgbModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { DashboardService } from 'src/app/shared/dashboard.service';
import { SharedModule } from 'src/app/shared/shared.module';
import { FeatureService } from '../feature.service';
import { IFeature } from '../interfaces';
import { FeatureWidgetComponent } from './feature-widget.component';
import {FeatureModule} from '../feature.module';

class MockFeatureService {
  mockFeatureDataEstimates = {
    result:
      {
        id: 'id',
        openEstimate: 1,
        inProgressEstimate: 2,
        completeEstimate: 3,
      }
  };

  mockFeatureDataIterations = {
    result: [
      {
        sName: 'name',
        changeDate: 'date',
        sUrl: 'url',
        sNumber: 'num',
        sEstimateTime: 'time',
        sStatus: 'Backlog',
      },
      {
        sStatus: 'In Progress',
        changeDate: 'date',
        sName: 'name',
        sUrl: 'url',
        sNumber: 'num',
        sEstimateTime: 'time',
      },
      {
        sStatus: 'Done',
        changeDate: 'date',
        sName: 'name',
        sUrl: 'url',
        sNumber: 'num',
        sEstimateTime: 'time',
      }
    ]
  };

  mockFeatureDataWip = {
    result: [
      {
        sEpicName: 'name',
        sEpicUrl: 'url',
        sEpicNumber: 'num',
        sEstimate: 'time',
      },
      {
        sEpicName: 'name',
        sEpicUrl: 'url',
        sEpicNumber: 'num',
        sEstimate: 'time',
      }
    ]
  };

  fetchAggregateSprintEstimates(): Observable<IFeature> {
    return of(this.mockFeatureDataEstimates.result);
  }

  fetchFeatureWip(): Observable<{ sEpicName: string; sEpicUrl: string; sEpicNumber: string; sEstimate: string}[]> {
    return of(this.mockFeatureDataWip.result);
  }

  fetchIterations(): Observable<{
    sName: string; changeDate: string; sUrl: string; sNumber: string; sEstimateTime: string; sStatus: string}[]> {
    return of(this.mockFeatureDataIterations.result);
  }
}

@NgModule({
  declarations: [],
  imports: [HttpClientTestingModule, SharedModule, CommonModule, BrowserAnimationsModule,
    RouterModule.forRoot([]), NgbModule, FeatureModule],
  entryComponents: []
})
class TestModule { }

describe('FeatureWidgetComponent', () => {
  let component: FeatureWidgetComponent;
  let featureService: FeatureService;
  let dashboardService: DashboardService;
  let modalService: NgbModal;
  let fixture: ComponentFixture<FeatureWidgetComponent>;

  const mockConfigEpics = {
    name: 'feature',
    options: {
      id: 'feature0',
      featureTool: 'feature',
      teamName: 'team',
      teamId: '1111',
      projectName: 'someProject',
      projectId: '2222',
      estimateMetricType: 'metric',
      sprintType: 'sprint',
      listType: 'epics',
    },
    componentId: '1234',
    collectorItemId: '5678'
  };

  const mockConfigIssues = {
    name: 'feature',
    options: {
      id: 'feature0',
      featureTool: 'feature',
      teamName: 'team',
      teamId: '1111',
      projectName: 'someProject',
      projectId: '2222',
      estimateMetricType: 'metric',
      sprintType: 'sprint',
      listType: 'issues',
    },
    componentId: '1234',
    collectorItemId: '5678'
  };

  /*const estimates = {
    id: '123',
    openEstimate: 1,
    inProgressEstimate: 2,
    completeEstimate: 3
  } as IFeature;*/

  const iterations = [[
    {
      sName: 'name',
      changeDate: 'date',
      sUrl: 'url',
      sNumber: 'num',
      sEstimateTime: 'time',
      sStatus: 'Backlog',
    },
    {
      sStatus: 'In Progress',
      changeDate: 'date',
      sName: 'name',
      sUrl: 'url',
      sNumber: 'num',
      sEstimateTime: 'time',
    },
    {
      sStatus: 'Done',
      changeDate: 'date',
      sName: 'name',
      sUrl: 'url',
      sNumber: 'num',
      sEstimateTime: 'time',
    }],
      [{
        sName: 'name',
        changeDate: 'date',
        sUrl: 'url',
        sNumber: 'num',
        sEstimateTime: 'time',
        sStatus: 'Backlog',
      },
      {
        sStatus: 'In Progress',
        changeDate: 'date',
        sName: 'name',
        sUrl: 'url',
        sNumber: 'num',
        sEstimateTime: 'time',
      },
      {
        sStatus: 'Done',
        changeDate: 'date',
        sName: 'name',
        sUrl: 'url',
        sNumber: 'num',
        sEstimateTime: 'time',
      }]];

  const wip = [
    [{
      sEpicName: 'name',
      sEpicUrl: 'url',
      sEpicNumber: 'num',
      sEstimate: 'time',
    },
    {
      sEpicName: 'name',
      sEpicUrl: 'url',
      sEpicNumber: 'num',
      sEstimate: 'time',
    }],
    [{
      sEpicName: 'name',
      sEpicUrl: 'url',
      sEpicNumber: 'num',
      sEstimate: 'time',
    },
      {
        sEpicName: 'name',
        sEpicUrl: 'url',
        sEpicNumber: 'num',
        sEstimate: 'time',
      }]
  ];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: FeatureService, useClass: MockFeatureService }
      ],
      imports: [
        TestModule, HttpClientTestingModule, SharedModule, CommonModule, BrowserAnimationsModule, RouterModule.forRoot([])
      ],
      declarations: [],
      schemas: [NO_ERRORS_SCHEMA]
    })
      .compileComponents();

  }));

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(featureService).toBeTruthy();
    expect(dashboardService).toBeTruthy();
    expect(modalService).toBeTruthy();
    expect(fixture).toBeTruthy();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FeatureWidgetComponent);
    component = fixture.componentInstance;
    featureService = TestBed.get(FeatureService);
    dashboardService = TestBed.get(DashboardService);
    modalService = TestBed.get(NgbModal);
    fixture.detectChanges();
  });

  it('should call ngOnInit', () => {
    component.ngOnInit();
  });

  it('should call ngAfterViewInit', () => {
    component.ngAfterViewInit();
  });

  it('should call startRefreshInterval', () => {
    spyOn(component, 'getCurrentWidgetConfig').and.returnValues(of(mockConfigEpics), of(mockConfigIssues), of(mockConfigIssues), of(null));
    component.startRefreshInterval();
  });

  it('should hit stopRefreshInterval', () => {
    component.stopRefreshInterval();
  });

  it('should generateIterationSummary', () => {
    component.generateIterationSummary(null);
  });

  it('should generateFeatureSummary with issues', () => {
    const params = {
      listType: 'issues',
      featureTool: 'featureTool',
      projectName: 'projectName',
      teamName: 'teamName'
    };

    component.generateFeatureSummary(iterations, params);
    component.generateFeatureSummary(null, params);
  });

  it('should generateFeatureSummary with epics', () => {
    const params = {
      listType: 'epics',
      featureTool: 'featureTool',
      projectName: 'projectName',
      teamName: 'teamName'
    };

    component.generateFeatureSummary(wip, params);
    component.generateFeatureSummary(null, params);
  });
});

