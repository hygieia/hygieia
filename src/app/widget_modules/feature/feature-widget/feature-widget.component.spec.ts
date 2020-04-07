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

class MockFeatureService {
  mockFeatureData = {
    result: [
      {
        id: 'id',
        openEstimate: 1,
        inProgressEstimate: 2,
        completeEstimate: 3,
      },
    ],
  };

  fetchDetails(): Observable<IFeature[]> {
    return of(this.mockFeatureData.result);
  }
}

@NgModule({
  declarations: [],
  imports: [HttpClientTestingModule, SharedModule, CommonModule, BrowserAnimationsModule, RouterModule.forRoot([]), NgbModule],
  entryComponents: []
})
class TestModule { }

describe('FeatureWidgetComponent', () => {
  let component: FeatureWidgetComponent;
  let featureService: FeatureService;
  let dashboardService: DashboardService;
  let modalService: NgbModal;
  let fixture: ComponentFixture<FeatureWidgetComponent>;

  const IFeatureTest = {
    id: '123',
    openEstimate: 1,
    inProgressEstimate: 2,
    completeEstimate: 3
  } as IFeature;

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

    fixture = TestBed.createComponent(FeatureWidgetComponent);
    component = fixture.componentInstance;
    featureService = TestBed.get(FeatureService);
    dashboardService = TestBed.get(DashboardService);
    modalService = TestBed.get(NgbModal);
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
    fixture.detectChanges();
  });

  it('should generateIterationSummary', () => {
    component.generateIterationSummary(IFeatureTest);
  });

  it('should generateFeatureSummary with issues', () => {
    const params = {
      listType: 'issues',
      featureTool: 'featureTool',
      projectName: 'projectName',
      teamName: 'teamName'
    };

    const iterations = [
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
      }];

    component.generateFeatureSummary(iterations, params);
  });

  it('should generateFeatureSummary with epics', () => {
    const params = {
      listType: 'epics',
      featureTool: 'featureTool',
      projectName: 'projectName',
      teamName: 'teamName'
    };

    const wip = [
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
      }];

    component.generateFeatureSummary(wip, params);
  });
});


