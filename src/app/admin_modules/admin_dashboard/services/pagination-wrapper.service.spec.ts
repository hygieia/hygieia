import { TestBed, inject } from '@angular/core/testing';
import { PaginationWrapperService } from './pagination-wrapper.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpClientModule } from '@angular/common/http';
import { DashboardDataService } from './dashboard-data.service';
import { AdminDashboardService } from './dashboard.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { MockDashboardDataService } from './mock-dashboard-data.service';

describe('PaginationWrapperService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [HttpClientTestingModule, HttpClientModule],
    providers: [PaginationWrapperService,
      { provide: DashboardDataService, useClass: MockDashboardDataService },
      AdminDashboardService, AuthService]

  }));

  it('should be created', () => {
    const service: PaginationWrapperService = TestBed.get(PaginationWrapperService);
    expect(service).toBeTruthy();
  });



  it('should be calculate Total Items in dashboard ',
    inject(
      [HttpTestingController, PaginationWrapperService],
      (
        httpMock: HttpTestingController,
        service: PaginationWrapperService
      ) => {
        service.calculateTotalItems('').subscribe((data: any) => {
          expect(data).toBeTruthy();
        });

      }
    )
  );


  it('should be get Total Items dashboard ',
    inject(
      [PaginationWrapperService],
      (
      ) => {
        const servicePage: PaginationWrapperService = TestBed.get(PaginationWrapperService);
        servicePage.totalItems = 200;
        expect(servicePage.getTotalItems()).toEqual(200);
      }
    )
  );

  it('should be page Change Handler ',
    inject(
      [ PaginationWrapperService],
      (
        service: PaginationWrapperService
      ) => {
        service.pageChangeHandler(1, '').subscribe((data: any) => {
          expect(data).toBeTruthy();
        });
      }
    )
  );


  it('should be page Change Handler For MyDash ',
  inject(
    [HttpTestingController, PaginationWrapperService],
    (
      httpMock: HttpTestingController,
      service: PaginationWrapperService
    ) => {
      service.pageChangeHandlerForMyDash(1, '').subscribe((data: any) => {
        expect(data).toBeTruthy();
      });
    }
  )
);


});
