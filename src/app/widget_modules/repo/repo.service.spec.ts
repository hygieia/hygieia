import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { inject, TestBed } from '@angular/core/testing';

import { RepoService } from './repo.service';

describe('RepoService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [
      HttpClientTestingModule
    ]
  }).compileComponents());

  it('should be created', () => {
    const service: RepoService = TestBed.get(RepoService);
    expect(service).toBeTruthy();
  });

  afterEach(inject([HttpTestingController], (httpMock: HttpTestingController) => {
    httpMock.verify();
  }));

  it('should fetch valid repo commit data',
    inject([HttpTestingController, RepoService],
      (httpMock: HttpTestingController, service: RepoService) => {
        // We call the service
        service.fetchCommits('123', 14).subscribe(data => {
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
                id: 'testId',
                collectorItemId: 'testId',
                scmRevisionNumber: 'testRev',
                scmAuthor: 'testAuthor',
                scmCommitLog: 'testCommit',
                scmCommitTimestamp: 'testTime',
                timestamp: 'testTime',
                number: 'testNum',
                mergeAuthor: 'testAuthor',
                mergedAt: 'testMerge'
              }
              ]
          }
        );
      })
  );

  it('should fetch valid repo pull data',
    inject([HttpTestingController, RepoService],
      (httpMock: HttpTestingController, service: RepoService) => {
        // We call the service
        service.fetchPullRequests('123', 14).subscribe(data => {
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
                id: 'testId',
                collectorItemId: 'testId',
                scmRevisionNumber: 'testRev',
                scmAuthor: 'testAuthor',
                scmCommitLog: 'testCommit',
                scmCommitTimestamp: 'testTime',
                timestamp: 'testTime',
                number: 'testNum',
                mergeAuthor: 'testAuthor',
                mergedAt: 'testMerge'
              }
            ]
          }
        );
      })
  );

  it('should fetch valid repo issue data',
    inject([HttpTestingController, RepoService],
      (httpMock: HttpTestingController, service: RepoService) => {
        // We call the service
        service.fetchIssues('123', 14).subscribe(data => {
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
                id: 'testId',
                collectorItemId: 'testId',
                scmRevisionNumber: 'testRev',
                scmAuthor: 'testAuthor',
                scmCommitLog: 'testCommit',
                scmCommitTimestamp: 'testTime',
                timestamp: 'testTime',
                number: 'testNum',
                mergeAuthor: 'testAuthor',
                mergedAt: 'testMerge'
              }
            ]
          }
        );
      })
  );
});
