import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SecurityScanWidgetComponent } from './security-scan-widget.component';
import { SecurityScanService } from '../security-scan.service';
import { DashboardService } from '../../../shared/dashboard.service';
import { NgbModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { ISecurityScanResponse } from '../security-scan-interfaces';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SharedModule } from '../../../shared/shared.module';
import { CommonModule } from '@angular/common';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { SecurityScanModule } from '../security-scan.module';
import { ICollItem } from 'src/app/viewer_modules/collector-item/interfaces';

class MockSecurityScanService {
  mockCollectorItemArray: ICollItem[] = [
    {
      collectorId: '5991223442ff4e0d3c1485c1',
      description: 'identity-profile-preferences-master',
      enabled: true,
      errorCount: 0,
      errors: [],
      id: '5cba241bb0dd131c5f3eeb34',
      lastUpdated: 1619112848762,
      options: {
        dashboardId: 'id',
        jobName: 'jobName',
        jobUrl: 'joburl.com',
        instanceUrl: 'instanceurl.com',
        branch: 'development',
        url: 'url.com',
        repoName: 'identitygithub.com',
        path: '/test',
        artifactName: 'artifactTest',
        password: 'pswrd',
        personalAccessToken: 'token'
      },
      pushed: false,
      refreshLink: '/security/refresh?projectName=identity-profile-preferences-master',
      niceName: 'nicename',
      environment: 'env'
    }
  ];

  mockSecurityScanData: ISecurityScanResponse = {
    result: [
      {
        id: 'testId',
        collectorItemId: 'testCollItemId',
        timestamp: 1234,
        type: 'SecurityAnalysis',
        metrics: [
          {
            name: 'High',
            value: '6',
            formattedValue: '6',
            status: 'Alert'
          },
          {
            name: 'Score',
            value: '62',
            formattedValue: '62',
            status: 'test'
          },
        ]
      }
    ],
    lastUpdated: '1234',
    reportUrl: 'https://testscan.com/testComponent/report.html'
  };



  getSecurityScanCollectorItems(componentId: string): Observable<ICollItem[]> {
    return of(this.mockCollectorItemArray as ICollItem[]);
  }

  refreshProject() {
    return 'Successfully refreshed';
  }

  getCodeQuality(componentId, collectorItemId: string): Observable<ISecurityScanResponse> {
    return of(this.mockSecurityScanData as ISecurityScanResponse);
  }
}

@NgModule({
  declarations: [],
  imports: [SecurityScanModule, HttpClientTestingModule, SharedModule, CommonModule, BrowserAnimationsModule,
    RouterModule.forRoot([]), NgbModule],
  entryComponents: []
})
class TestModule { }

describe('SecurityScanWidgetComponent', () => {
  let component: SecurityScanWidgetComponent;
  let securityScanService: SecurityScanService;
  let dashboardService: DashboardService;
  let modalService: NgbModule;
  let fixture: ComponentFixture<SecurityScanWidgetComponent>;

  const mockCollectorItemArray: ICollItem[] = [
    {
      collectorId: '5991223442ff4e0d3c1485c1',
      description: 'identity-profile-preferences-master',
      enabled: true,
      errorCount: 0,
      errors: [],
      id: '5cba241bb0dd131c5f3eeb34',
      lastUpdated: 1619112848762,
      options: {
        dashboardId: 'id',
        jobName: 'jobName',
        jobUrl: 'joburl.com',
        instanceUrl: 'instanceurl.com',
        branch: 'development',
        url: 'url.com',
        repoName: 'identitygithub.com',
        path: '/test',
        artifactName: 'artifactTest',
        password: 'pswrd',
        personalAccessToken: 'token'
      },
      pushed: false,
      refreshLink: '/security/refresh?projectName=identity-profile-preferences-master',
      niceName: 'nicename',
      environment: 'env'
    }
  ];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: SecurityScanService, useClass: MockSecurityScanService }
      ],
      imports: [TestModule, HttpClientTestingModule, SharedModule, CommonModule, BrowserAnimationsModule, RouterModule.forRoot([])],
      declarations: [],
      schemas: [NO_ERRORS_SCHEMA]
    })
      .compileComponents();

  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SecurityScanWidgetComponent);
    component = fixture.componentInstance;
    securityScanService = TestBed.get(SecurityScanService);
    dashboardService = TestBed.get(DashboardService);
    modalService = TestBed.get(NgbModal);
    fixture.detectChanges();
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(securityScanService).toBeTruthy();
    expect(dashboardService).toBeTruthy();
    expect(modalService).toBeTruthy();
  });

  it('should assign default if no data', () => {
    component.hasData = false;
    component.charts = [];
    component.setDefaultIfNoData();
    expect(component.charts[0].data.items[0].title).toEqual('No Data Found');
  });

  it('should not assign default if it has data', () => {

    component.hasData = true;
    component.setDefaultIfNoData();
    expect(component.charts).toEqual([]);
  });

  it('should call ngOnInit()', () => {
    component.ngOnInit();
  });

  it('should call ngOnDestroy', () => {
    component.ngOnDestroy();
  });

  it('should hit stopRefreshInterval', () => {
    component.stopRefreshInterval();
  });

  it('should call ngAfterViewInit', () => {
    component.ngAfterViewInit();
  });



  it('should return empty on refresh if !hasData', () => {
    (component as any).params = { componentId: '1234' };
    component.loadCharts(mockCollectorItemArray, 0);
    component.hasData = false;
    component.refreshProject();
  });

  it('should loadCharts', () => {
    (component as any).params = { componentId: '1234' };
    component.loadCharts(mockCollectorItemArray, 0);
  });

  it('should hit startRefreshInterval', () => {
    const mockConfig = {
      name: 'codeanalysis',
      options: {
        id: 'codeanalysis0',
      },
      componentId: '1234',
      collectorItemId: '5678'
    };

    spyOn(component, 'getCurrentWidgetConfig').and.returnValues(of(mockConfig), of(mockConfig), of(null));
    spyOn(securityScanService, 'getSecurityScanCollectorItems').and.returnValues(of(mockCollectorItemArray), of([]));
    spyOn(dashboardService, 'checkCollectorItemTypeExist').and.returnValues(true, false);
    component.startRefreshInterval();
    component.startRefreshInterval();
    component.startRefreshInterval();
  });
});
