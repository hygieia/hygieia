import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { inject, TestBed } from '@angular/core/testing';

import { TestService } from './test.service';
import { TestType } from './interfaces';

describe('test result service', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [
      HttpClientTestingModule
    ]
  }).compileComponents());

  it('should be created', () => {
    const service: TestService = TestBed.get(TestService);
    expect(service).toBeTruthy();
  });

  it('should fetch valid code quality data',
    inject([HttpTestingController, TestService],
      (httpMock: HttpTestingController, service: TestService) => {
        // We call the service
        service.fetchTestResults('123', 1, 3, [TestType.Functional, TestType.Performance]).subscribe(data => {
          expect(data).toBeTruthy();
        });
        // We set the expectations for the HttpClient mock
        const request = httpMock
          .expectOne(req => req.method === 'GET');
        expect(request.request.method).toEqual('GET');
        // Then we set the fake data to be returned by the mock
        request.flush(
          {
            result: [
                {
                    id: '123',
                    collectorItemId: '123',
                    timestamp: 1554902828596,
                    executionId: '3',
                    buildId: '123',
                    description: 'description',
                    url: 'url',
                    startTime: 1554902456534,
                    endTime: 1554902464256,
                    duration: 7722,
                    failureCount: 0,
                    successCount: 1,
                    skippedCount: 0,
                    totalCount: 1,
                    unknownStatusCount: 0,
                    type: 'Functional',
                    targetAppName: 'hygieiaTestCuc',
                    targetEnvName: 'dev',
                    testCapabilities: [
                        {
                            timestamp: 0,
                            executionId: '3',
                            description: '/test',
                            startTime: 0,
                            endTime: 0,
                            duration: 0,
                            failedTestSuiteCount: 0,
                            successTestSuiteCount: 1,
                            skippedTestSuiteCount: 0,
                            totalTestSuiteCount: 1,
                            unknownStatusTestSuiteCount: 0,
                            status: 'Success',
                            type: 'Functional',
                            testSuites: [],
                            tags: []
                        }
                    ]
                }
            ]
          }
        );
      })
  );

  afterEach(inject([HttpTestingController], (httpMock: HttpTestingController) => {
    httpMock.verify();
  }));
});
