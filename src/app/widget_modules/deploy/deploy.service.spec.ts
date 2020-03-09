import { TestBed, inject } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import {HttpClientModule} from '@angular/common/http';
import { DeployService } from './deploy.service';

describe('DeployService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [HttpClientTestingModule, HttpClientModule],
    providers: [DeployService]
  }));

  it('should be created', () => {
    const service: DeployService = TestBed.get(DeployService);
    expect(service).toBeTruthy();
  });

  it('should fetch valid deploy data',
    inject([HttpTestingController, DeployService],
      (httpMock: HttpTestingController, service: DeployService) => {
        // We call the service
        service.fetchDetails('59f88f5e6a3cf205f312c62e').subscribe(data => {
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
                name : 'QA',
                url : 'mydeployurl.com/something/something',
                units : [
                  {
                    name : 'api.jar',
                    version : '2.0.5',
                    jobURL: 'mydeployurl.com/something/something',
                    deployed : true,
                    lastUpdated : 1529001705028,
                    servers : [
                      {
                        name : 'msp_tetris_dws_04',
                        online : false,
                      }
                    ],
                  }
                ],
                lastUpdated : 1560280286912
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
