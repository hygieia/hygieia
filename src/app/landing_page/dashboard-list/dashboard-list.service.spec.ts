import { of } from 'rxjs';
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { DashboardListService } from './dashboard-list.service';

describe('DashboardListService', () => {
  let dashboardListService: DashboardListService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DashboardListService],
      imports: [ HttpClientTestingModule]
    });

    dashboardListService = TestBed.get(DashboardListService);
  });
  it('should be created', () => {
    expect(dashboardListService).toBeTruthy();
  });
  it('should call getAllDashboards', () => {
    const dashboardListResponse = {
        data: [
          { title: 'test'},
          { title: 'test1'},
          { title: 'test2'}
        ],
        total: '3'
      };
    let response;
    spyOn(dashboardListService, 'getAllDashboards').and.returnValue(of(dashboardListResponse));

    dashboardListService.getAllDashboards(null).subscribe(res => {
      response = res;
    });

    expect(response).toEqual(dashboardListResponse);
  });
  it('should call getMyDashboards', () => {
    const dashboardListResponse = {
        data: [
          { title: 'test'},
          { title: 'test1'},
          { title: 'test2'}
        ],
        total: '3'
      };
    let response;
    spyOn(dashboardListService, 'getMyDashboards').and.returnValue(of(dashboardListResponse));

    dashboardListService.getMyDashboards(null).subscribe(res => {
      response = res;
    });

    expect(response).toEqual(dashboardListResponse);
  });
});
