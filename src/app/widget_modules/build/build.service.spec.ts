import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { inject, TestBed } from '@angular/core/testing';

import { BuildService } from './build.service';

describe('BuildService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [
      HttpClientTestingModule
    ]
  }).compileComponents());

  it('should be created', () => {
    const service: BuildService = TestBed.get(BuildService);
    expect(service).toBeTruthy();
  });

  it('should fetch valid build data',
    inject([HttpTestingController, BuildService],
      (httpMock: HttpTestingController, service: BuildService) => {
        // We call the service
        service.fetchDetails('123', 14).subscribe(data => {
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
                timestamp: 1550764955300,
                number: '123',
                buildUrl: 'https://jenkins.com',
                startTime: 1550764130294,
                endTime: 1550764955142,
                duration: 824848,
                buildStatus: 'Success',
                codeRepos: [
                  {
                    url: 'https://github.com',
                    branch: 'master',
                    type: 'GIT'
                  }
                ],
                sourceChangeSet: []
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
