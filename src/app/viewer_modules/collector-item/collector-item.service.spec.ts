import {inject, TestBed} from '@angular/core/testing';

import { CollectorItemService } from './collector-item.service';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

/*class TestModule { }

class MockCIService {
  mockData: IDashboardCI;

  getDashboardByCI(ci: string): Observable<IDashboardCI[]> {
    if (!ci) { return of([]); }

    // @ts-ignore
    this.mockData = {
        id: 'dummyID',
        template: 'CapOne',
        title: 'Hygieia',
        type: 'Team',
        widgets: [],
        owner: 'owner',
        owners: [],
        application: {
          name: 'Hygieia',
          owner: 'owner',
          lineOfBusiness: 'LOB',
          components: [
            {
              id: 'compID',
              name: 'compName',
              collectorItems: {
                CMDB: [],
                Incident: [],
                Build: [],
                Artifact: [],
                Deployment: [],
                AgileTool: [],
                Feature: [], // Deprecated
                TestResult: [],
                ScopeOwner: [], // Deprecated
                Scope: [], // Deprecated
                CodeQuality: [],
                Test: [],
                StaticSecurityScan: [],
                LibraryPolicy: [],
                ChatOps: [],
                Cloud: [],
                Product: [],
                AppPerformance: [],
                InfraPerformance: [],
                Score: [],
                TEAM: [],
                Log: [],
                AutoDiscover: [],
                SCM: [
                  {
                    id: 'scmID',
                    description: 'Registered by tester.',
                    niceName: 'dummyData',
                    environment: 'dummyData',
                    enabled: true,
                    errors: [],
                    pushed: false,
                    collectorId: 'collectorID',
                    lastUpdated: 1543728992481,
                    options: {
                      dashboardId: 'dummyData',
                      jobName: 'dummyData',
                      jobUrl: 'dummyData',
                      instanceUrl: 'dummyData',
                      repoName: 'dummyData',
                      path: 'dummyData',
                      artifactName: 'dummyData',
                      branch: 'master',
                      url: 'https://github..com/someRepoURL'
                    },
                    errorCount: 0
                  }
                ],
                Audit: [
                  {
                    id: 'auditID',
                    description: 'all audit process results',
                    niceName: 'dummyData',
                    environment: 'dummyData',
                    enabled: true,
                    errors: [],
                    pushed: false,
                    collectorId: 'collectorID',
                    lastUpdated: 1587407312851,
                    options: {
                      dashboardId: 'dashBdID',
                      jobName: 'dummyData',
                      jobUrl: 'dummyData',
                      instanceUrl: 'dummyData',
                      repoName: 'dummyData',
                      path: 'dummyData',
                      artifactName: 'dummyData',
                      branch: 'master',
                      url: 'https://github..com/someRepoURL'
                    },
                    errorCount: 0
                  }
                ]
              }
            }
          ]
        },
        configurationItemBusServName: 'CIBusServName',
        configurationItemBusAppName: 'CIBusAppName',
        validServiceName: true,
        validAppName: true,
        remoteCreated: false,
        scoreEnabled: false,
        scoreDisplay: 'HEADER',
        activeWidgets: [],
        createdAt: 0,
        updatedAt: 0,
        errorCode: 0
      };

    return of([this.mockData]);
  }
}*/

describe('CollectorItemService', () => {

  beforeEach(() => TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ]
    }).compileComponents()

  );

  it('should be created', () => {
    const service: CollectorItemService = TestBed.get(CollectorItemService);
    expect(service).toBeTruthy();
  });


  it('should get valid Dashboard data By CI',
    inject([HttpTestingController, CollectorItemService],
      (httpMock: HttpTestingController, service: CollectorItemService) => {
        // call the service - happy path
        service.getDashboardByCI('Hygieia' ).subscribe(data => {
          expect(data).toBeTruthy();
        });
        // call the service - with empty input
        service.getDashboardByCI( null ).subscribe(data => {
          expect(data).toBeTruthy();
        });
        // We set the expectations for the HttpClient mock
        const request = httpMock.expectOne(req => req.method === 'GET');
        expect(request.request.method).toEqual('GET');
        // Then we set the fake data to be returned by the mock
        request.flush(
          {
            result: [ ],
            lastUpdated: 1590603518297
          }
        );
      })
  );


  it('should get valid Dashboard data by title',
    inject([HttpTestingController, CollectorItemService],
      (httpMock: HttpTestingController, service: CollectorItemService) => {
        // call the service - happy path
        service.getDashboardByTitle('Hygieia' ).subscribe(data => {
          expect(data).toBeTruthy();
        });

        // call the service - with empty input
        service.getDashboardByTitle( null ).subscribe(data => {
          expect(data).toBeTruthy();
        });
        // We set the expectations for the HttpClient mock
        const request = httpMock.expectOne(req => req.method === 'GET');
        expect(request.request.method).toEqual('GET');
        // Then we set the fake data to be returned by the mock
        request.flush(
          {
            result: [ ],
            lastUpdated: 1590603518297
          }
        );
      })
  );


  it('should get valid Audit data',
    inject([HttpTestingController, CollectorItemService],
      (httpMock: HttpTestingController, service: CollectorItemService) => {
        // call the service for collector: Audit
        service.getCollectorItemDetails('Hygieia', 'HygieiaComp', 'Audit' ).subscribe(data => {
          expect(data).toBeTruthy();
        });
        // We set the expectations for the HttpClient mock
        const request = httpMock.expectOne(req => req.method === 'GET');
        expect(request.request.method).toEqual('GET');
        // Then we set the fake data to be returned by the mock
        request.flush(
          {
            result: [ ],
            lastUpdated: 1590603518297
          }
        );
      })
  );


  it('should get no Audit data for null component',
    inject([HttpTestingController, CollectorItemService],
      (httpMock: HttpTestingController, service: CollectorItemService) => {
        // call the service for collector: Audit, with null componentId
        service.getCollectorItemDetails('Hygieia', null, 'Audit').subscribe(data => {
          expect(data).toBeTruthy();
        });
        const request = httpMock.expectOne(req => req.method === 'GET');
        expect(request.request.method).toEqual('GET');
        // Then we set the fake data to be returned by the mock
        request.flush(
          {
            result: [ ],
            lastUpdated: 1590603518297
          }
        );
      })
  );

  it('should get Artifact data',
    inject([HttpTestingController, CollectorItemService],
      (httpMock: HttpTestingController, service: CollectorItemService) => {
        // call the service for collector: Artifact
        service.getCollectorItemDetails('Hygieia', 'HygieiaComp', 'Artifact').subscribe(data => {
          expect(data).toBeTruthy();
        });
      })
  );

  it('should get no Artifact data for null component',
    inject([HttpTestingController, CollectorItemService],
      (httpMock: HttpTestingController, service: CollectorItemService) => {
        // call the service for collector: Artifact, with null componentId
        service.getCollectorItemDetails('Hygieia', null, '').subscribe(data => {
          expect(data).toBeTruthy();
        });
      })
  );

  it('should get valid Build data',
    inject([HttpTestingController, CollectorItemService],
      (httpMock: HttpTestingController, service: CollectorItemService) => {
        // call the service for collector: Build
        service.getCollectorItemDetails('Hygieia', 'HygieiaComp', 'Build').subscribe(data => {
          expect(data).toBeTruthy();
        });
        // call the service for collector: Build, with null componentId
        service.getCollectorItemDetails('Hygieia', null, 'Build').subscribe(data => {
          expect(data).toBeTruthy();
        });
        // We set the expectations for the HttpClient mock
        const request = httpMock.expectOne(req => req.method === 'GET');
        expect(request.request.method).toEqual('GET');
        // Then we set the fake data to be returned by the mock
        request.flush(
          {
            result: [ ],
            lastUpdated: 1590603518297
          }
        );
      })
  );

  it('should get valid Code Quality data',
    inject([HttpTestingController, CollectorItemService],
      (httpMock: HttpTestingController, service: CollectorItemService) => {
        // call the service for collector: CodeQuality
        service.getCollectorItemDetails('Hygieia', 'HygieiaComp', 'CodeQuality').subscribe(data => {
          expect(data).toBeTruthy();
        });
        // call the service for collector: CodeQuality, with null componentId
        service.getCollectorItemDetails('Hygieia', null, 'CodeQuality').subscribe(data => {
          expect(data).toBeTruthy();
        });
        // We set the expectations for the HttpClient mock
        const request = httpMock.expectOne(req => req.method === 'GET');
        expect(request.request.method).toEqual('GET');
        // Then we set the fake data to be returned by the mock
        request.flush(
          {
            result: [ ],
            lastUpdated: 1590603518297
          }
        );
      })
  );

  it('should get valid Deployment data',
    inject([HttpTestingController, CollectorItemService],
      (httpMock: HttpTestingController, service: CollectorItemService) => {
        // call the service for collector: Deployment
        service.getCollectorItemDetails('Hygieia', 'HygieiaComp', 'Deployment').subscribe(data => {
          expect(data).toBeTruthy();
        });
        // call the service for collector: Deployment, with null componentId
        service.getCollectorItemDetails('Hygieia', null, 'Deployment').subscribe(data => {
          expect(data).toBeTruthy();
        });
        // We set the expectations for the HttpClient mock
        const request = httpMock.expectOne(req => req.method === 'GET');
        expect(request.request.method).toEqual('GET');
        // Then we set the fake data to be returned by the mock
        request.flush(
          {
            result: [ ],
            lastUpdated: 1590603518297
          }
        );
      })
  );

  it('should get Incident data',
    inject([HttpTestingController, CollectorItemService],
      (httpMock: HttpTestingController, service: CollectorItemService) => {
        // call the service for collector: Incident
        service.getCollectorItemDetails('Hygieia', 'HygieiaComp', 'Incident').subscribe(data => {
          expect(data).toBeTruthy();
        });
        // call the service for collector: Incident, with null componentId
        service.getCollectorItemDetails('Hygieia', null, 'Incident').subscribe(data => {
          expect(data).toBeTruthy();
        });
      })
  );


  it('should get valid OSS data',
    inject([HttpTestingController, CollectorItemService],
      (httpMock: HttpTestingController, service: CollectorItemService) => {
        // call the service for collector: LibraryPolicy
        service.getCollectorItemDetails('Hygieia', 'HygieiaComp', 'LibraryPolicy').subscribe(data => {
          expect(data).toBeTruthy();
        });
        // call the service for collector: LibraryPolicy, with null componentId
        service.getCollectorItemDetails('Hygieia', null, 'LibraryPolicy').subscribe(data => {
          expect(data).toBeTruthy();
        });
        // We set the expectations for the HttpClient mock
        const request = httpMock.expectOne(req => req.method === 'GET');
        expect(request.request.method).toEqual('GET');
        // Then we set the fake data to be returned by the mock
        request.flush(
          {
            result: [ ],
            lastUpdated: 1590603518297
          }
        );
      })
  );

  it('should get valid SCM data',
    inject([HttpTestingController, CollectorItemService],
      (httpMock: HttpTestingController, service: CollectorItemService) => {
        // call the service for collector: SCM
        service.getCollectorItemDetails('Hygieia', 'HygieiaComp', 'SCM').subscribe(data => {
          expect(data).toBeTruthy();
        });
        // We set the expectations for the HttpClient mock
        const calls = httpMock.match((request) => {
          return (request.url === '/api/commit/' ||
            request.url === '/api/gitrequests/type/pull/state/all/' ||
            request.url === '/api/gitrequests/type/issue/state/all/');
        });
        expect(calls.length === 3);
        calls.forEach( call => call.flush( {
            result: [ ],
            lastUpdated: 1590603518297
          }
        ));
      })
  );

  it('should get no SCM data',
    inject([HttpTestingController, CollectorItemService],
      (httpMock: HttpTestingController, service: CollectorItemService) => {
        // call the service for collector: SCM, with null componentId
        service.getCollectorItemDetails('Hygieia', null, 'SCM').subscribe(data => {
          expect(data).toBeTruthy();
        });
      })
  );

  it('should get valid StaticSecurityScan data',
    inject([HttpTestingController, CollectorItemService],
      (httpMock: HttpTestingController, service: CollectorItemService) => {
        // call the service for collector: StaticSecurityScan
        service.getCollectorItemDetails('Hygieia', 'HygieiaComp', 'StaticSecurityScan').subscribe(data => {
          expect(data).toBeTruthy();
        });
        // call the service for collector: StaticSecurityScan, with null componentId
        service.getCollectorItemDetails('Hygieia', null, 'StaticSecurityScan').subscribe(data => {
          expect(data).toBeTruthy();
        });
        // We set the expectations for the HttpClient mock
        const request = httpMock.expectOne(req => req.method === 'GET');
        expect(request.request.method).toEqual('GET');
        // Then we set the fake data to be returned by the mock
        request.flush(
          {
            result: [ ],
            lastUpdated: 1590603518297
          }
        );
      })
  );

  it('should get valid Test data',
    inject([HttpTestingController, CollectorItemService],
      (httpMock: HttpTestingController, service: CollectorItemService) => {
        // call the service for collector: Test
        service.getCollectorItemDetails('Hygieia', 'HygieiaComp', 'Test').subscribe(data => {
          expect(data).toBeTruthy();
        });
        // We set the expectations for the HttpClient mock
        const request = httpMock.expectOne(req => req.method === 'GET');
        expect(request.request.method).toEqual('GET');
        // Then we set the fake data to be returned by the mock
        request.flush(
          {
            result: [ ],
            lastUpdated: 1590603518297
          }
        );
      })
  );

  it('should get empty Test data',
    inject([HttpTestingController, CollectorItemService],
      (httpMock: HttpTestingController, service: CollectorItemService) => {
        // call the service for collector: Test, with null componentId
        service.getCollectorItemDetails('Hygieia', null, 'Test').subscribe(data => {
          expect(data).toBeTruthy();
        });
        httpMock.expectOne(req => req.method === 'GET');
      })
  );

  it('should get no data for random/null collector',
    inject([HttpTestingController, CollectorItemService],
      (httpMock: HttpTestingController, service: CollectorItemService) => {
        // call the service - with random collector
        service.getCollectorItemDetails('Hygieia', 'HygieiaComp', 'random').subscribe(data => {
          expect(data).toBeTruthy();
        });
        // call the service - with null collector
        service.getCollectorItemDetails('Hygieia', 'HygieiaComp', null).subscribe(data => {
          expect(data).toBeTruthy();
        });
      })
  );

  afterEach(inject([HttpTestingController], (httpMock: HttpTestingController) => {
    httpMock.verify();
  }));
});
