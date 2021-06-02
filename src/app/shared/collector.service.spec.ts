import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { inject, TestBed } from '@angular/core/testing';

import { CollectorService } from './collector.service';
import { SEARCH_MOCK } from './collector.service.mockdata';

describe('CollectorService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [HttpClientTestingModule]
  }));

  it('should be created', () => {
    const service: CollectorService = TestBed.get(CollectorService);
    expect(service).toBeTruthy();
  });

  it('should fetch searched collector items',
    inject([HttpTestingController, CollectorService],
      (httpMock: HttpTestingController, service: CollectorService) => {
        service.searchItems('build', 'shared').subscribe(data => {
          expect(data).toBeTruthy();
          expect(data.length).toBe(20);
        });

        const request = httpMock.expectOne(req => req.method === 'GET');
        request.flush(SEARCH_MOCK);
      })
  );

  it('should fetch collector items by id',
    inject([HttpTestingController, CollectorService],
      (httpMock: HttpTestingController, service: CollectorService) => {
        service.getItemsById('123').subscribe(data => {
          expect(data).toBeTruthy();
        });

        const request = httpMock.expectOne(req => req.method === 'GET');
        request.flush(SEARCH_MOCK);
      })
  );

  it('should fetch item by search field',
    inject([HttpTestingController, CollectorService],
      (httpMock: HttpTestingController, service: CollectorService) => {
        service.getItemsByTypeBySearchField('type', 'filter').subscribe(data => {
          expect(data).toBeTruthy();
        });

        const request = httpMock.expectOne(req => req.method === 'GET');
        request.flush(SEARCH_MOCK);
      })
  );

});
