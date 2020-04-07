import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { inject, TestBed } from '@angular/core/testing';
import { FeatureService } from './feature.service';

describe('FeatureService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [
      HttpClientTestingModule
    ]
  }).compileComponents());

  it('should be created', () => {
    const service: FeatureService = TestBed.get(FeatureService);
    expect(service).toBeTruthy();
  });

  afterEach(inject([HttpTestingController], (httpMock: HttpTestingController) => {
    httpMock.verify();
  }));

  it('should fetch valid feature Aggregate Sprint Estimates',
    inject([HttpTestingController, FeatureService],
      (httpMock: HttpTestingController, service: FeatureService) => {
        // We call the service
        service.fetchAggregateSprintEstimates('123', 'teamId', 'projectId', 'agileType').subscribe(data => {
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
                id: 'id',
                openEstimate: 123,
                inProgressEstimate: 123,
                completeEstimate: 123,
              }
            ]
          }
        );
      })
  );

  it('should fetch valid feature wip',
    inject([HttpTestingController, FeatureService],
      (httpMock: HttpTestingController, service: FeatureService) => {
        // We call the service
        service.fetchFeatureWip('123', 'teamId', 'projectId', 'agileType').subscribe(data => {
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
                id: 'id',
                openEstimate: 123,
                inProgressEstimate: 123,
                completeEstimate: 123,
              }
            ]
          }
        );
      })
  );

  it('should fetch valid feature iterations',
    inject([HttpTestingController, FeatureService],
      (httpMock: HttpTestingController, service: FeatureService) => {
        // We call the service
        service.fetchIterations('123', 'teamId', 'projectId', 'agileType').subscribe(data => {
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
                id: 'id',
                openEstimate: 123,
                inProgressEstimate: 123,
                completeEstimate: 123,
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
