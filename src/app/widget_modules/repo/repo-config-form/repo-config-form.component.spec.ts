import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import {NgbActiveModal, NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { SharedModule } from 'src/app/shared/shared.module';
import {RepoConfigFormComponent} from './repo-config-form.component';
import {Observable, of} from 'rxjs';
import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {RouterModule} from '@angular/router';
import {DashboardService} from '../../../shared/dashboard.service';
import {CollectorService} from '../../../shared/collector.service';
import {RepoModule} from '../repo.module';

class MockCollectorService {
  mockCollectorData = {
    id: '4321',
    description: 'Github : repo1',
    collectorId: '1234',
    collector: {
      id: '1234',
      name: 'Github',
      collectorType: 'SCM'
    }
  };

  mockRepoArray = [
    {
      id : 'testId',
      description : 'testd desc.',
      enabled : true,
      errors : [],
      pushed : false,
      collectorId : '12345',
      lastUpdated : 12345,
      options : {
          branch : 'testBranch',
          url : 'testUrl'
      },
      upsertTime : '2021-02-21T15:30:02.885Z',
      _class : 'GitHubRepo'
  }
  ];

  mockCollectorType = [{name: 'test', collectorType: 'SCM'}];

  getItemsById(id: string): Observable<any> {
    return of(this.mockCollectorData);
  }

  collectorsByType(collectorType): Observable<any> {
    return of(this.mockCollectorType);
  }

  searchItemsBySearchField(type, search, field): Observable<any> {
    if (search === 'badSearch') {
      return of([]);
    }
    return of(this.mockRepoArray);
  }
}

class MockDashboardService {
  mockDashboard = {
    title: 'dashboard1',
    application: {
      components: [{
        collectorItems: {
          SCM: [{
            id: '1234',
            description: 'Github : repo1'
          }]
        }
      }]
    }
  };
  dashboardConfig$ = of(this.mockDashboard);
}

@NgModule({
  declarations: [],
  imports: [RepoModule, HttpClientTestingModule, SharedModule, CommonModule, BrowserAnimationsModule,
    RouterModule.forRoot([]), NgbModule],
  entryComponents: []
})
class TestModule { }

describe('RepoConfigFormComponent', () => {
  let component: RepoConfigFormComponent;
  let fixture: ComponentFixture<RepoConfigFormComponent>;
  let dashboardService: DashboardService;
  let collectorService: CollectorService;
  let modalService: NgbModule;

  const widgetConfigData = {
    options: {
      id: 'testId',
      scm: {
        name: 'scmName',
        value: 'value',
      },
      url: {
        options: {
          url: 'testUrl',
          branch: 'testBranch'
        }
      },
      branch: 'testBranch',
      userID: 'testUser',
      password: 'testPass',
      personalAccessToken: 'testPersonalAccess',
    }
  };

  const mockRepoArray = [
    {
      id : 'testId',
      description : 'testd desc.',
      enabled : true,
      errors : [],
      pushed : false,
      collectorId : '12345',
      lastUpdated : 12345,
      options : {
          branch : 'testBranch',
          url : 'testUrl'
      },
      upsertTime : '2021-02-21T15:30:02.885Z',
      _class : 'GitHubRepo'
  }
  ];

  const widgetConfigDataNoId = {
    options: {
      id: null,
      scm: {
        name: 'scmName',
        value: 'value',
      },
      url: 'testUrl',
      branch: 'testBranch',
      userID: 'testUser',
      password: 'testPass',
      personalAccessToken: 'testPersonalAccess',
    }
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [TestModule, ReactiveFormsModule, NgbModule, SharedModule, HttpClientTestingModule],
      declarations: [ ],
      providers: [
        { provide: NgbActiveModal, useClass: NgbActiveModal },
        { provide: CollectorService, useClass: MockCollectorService},
        { provide: DashboardService, useClass: MockDashboardService}
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepoConfigFormComponent);
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
    component.widgetConfig = null;
    component.widgetConfig = widgetConfigData;
  });

  it('should call ngOnInit()', () => {
    component.ngOnInit();
  });

  it('should assign selected job after submit', () => {
    component.createForm();
    expect(component.repoConfigForm.get('url').value).toEqual('');
    component.widgetConfig = widgetConfigData;
    component.submitForm();
    expect(component.repoConfigForm.get('url').value.options.url).toEqual('testUrl');

    component.createForm();
    component.widgetConfig = widgetConfigDataNoId;
    component.submitForm();
  });

  it('should return if no options field on url value', () => {
    component.createForm();
    component.widgetConfig = widgetConfigDataNoId;
    component.submitForm();
    expect(component.submitFailed).toEqual(true);
  });

  it('should return if empty response from search', () => {
    component.createForm();
    const badConfig = widgetConfigData;
    badConfig.options.url.options.url = 'badSearch';
    component.widgetConfig = badConfig;
    component.submitForm();
    expect(component.submitFailed).toEqual(true);
  });

  it('should return if collectorItem is null', () => {
    component.getRepoTitle(mockRepoArray[0]);
  });
});
