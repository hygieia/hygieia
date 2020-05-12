import { TestBed, inject } from '@angular/core/testing';

import { AdminDashboardService } from './dashboard.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HttpClientModule } from '@angular/common/http';
import { DASHBOARDDATA, DASHBOARDITEM } from './user-data.service.mockdata';

describe('AdminDashboardService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [HttpClientTestingModule, HttpClientModule],
    providers: [AdminDashboardService]
  }));

  it('should be created', () => {
    const service: AdminDashboardService = TestBed.get(AdminDashboardService);
    expect(service).toBeTruthy();
  });



  it('should be Get AdminDashboardService List',
    inject(
      [AdminDashboardService],
      (
        service: AdminDashboardService
      ) => {
        const data = service.getDashboardTitle(DASHBOARDDATA[0]);
        expect(data).toBeTruthy();

      }
    )
  );

  it('should be set BusinessApplicationId',
    inject(
      [AdminDashboardService],
      (
        service: AdminDashboardService
      ) => {
        service.setBusinessApplicationId('56d9cf6b7fab7c42f5918b84');
        expect('56d9cf6b7fab7c42f5918b84').toEqual(service.businessApplicationId);

      }
    )
  );

  it('should be set BusinessServiceId',
    inject(
      [AdminDashboardService],
      (
        service: AdminDashboardService
      ) => {
        service.setBusinessServiceId('56d9cf6b7fab7c42f5918b84');
        expect('56d9cf6b7fab7c42f5918b84').toEqual(service.businessServiceId);

      }
    )
  );
  it('should be get BusAppToolTipText',
    inject(
      [AdminDashboardService],
      (
        service: AdminDashboardService
      ) => {
        service.getBusAppToolTipText();
        expect('A Business Application (BAP) CI is a CI Subtype in the application which supports business function (Top level).')
          .toEqual(service.getBusAppToolTipText());

      }
    )
  );

  it('should be get BusSerToolTipText',
    inject(
      [AdminDashboardService],
      (
        service: AdminDashboardService
      ) => {
        service.getBusSerToolTipText();
        expect('A top level name which support Business function.')
          .toEqual(service.getBusSerToolTipText());

      }
    )
  );

  it('should be get getDashboardTitleOrig',
    inject(
      [AdminDashboardService],
      (
        service: AdminDashboardService
      ) => {
        const result = service.getDashboardTitleOrig(DASHBOARDITEM);
        expect('test')
          .toEqual(result);
      }
    )
  );

  it('should be get BusServValueBasedOnType',
    inject(
      [AdminDashboardService],
      (
        service: AdminDashboardService
      ) => {
        service.getBusServValueBasedOnType(DASHBOARDDATA[0], 'product');
        expect('product')
          .toEqual(service.getBusServValueBasedOnType(DASHBOARDDATA[0], 'product'));
      }
    )
  );


  it('should be get BusinessServiceId',
    inject(
      [AdminDashboardService],
      (
        service: AdminDashboardService
      ) => {
        service.setBusinessServiceId('56d9cf6b7fab7c42f5918b84');
        service.getBusinessServiceId('Test');
        expect('56d9cf6b7fab7c42f5918b84').toEqual(service.businessServiceId);

      }
    )
  );


  it('should be get BusinessApplicationId',
    inject(
      [AdminDashboardService],
      (
        service: AdminDashboardService
      ) => {
        service.setBusinessApplicationId('56d9cf6b7fab7c42f5918b84');
        service.getBusinessApplicationId('Test');
        expect('56d9cf6b7fab7c42f5918b84').toEqual(service.businessApplicationId);

      }
    )
  );

});
