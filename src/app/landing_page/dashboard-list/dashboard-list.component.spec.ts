import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { SharedModule } from '../../shared/shared.module';
import { DashboardListComponent } from './dashboard-list.component';
import { DashboardListService } from './dashboard-list.service';
import { IPaginationParams } from '../../shared/interfaces';

describe('DashboardListComponent', () => {
  let component: DashboardListComponent;
  let fixture: ComponentFixture<DashboardListComponent>;
  let dashboardListService;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, SharedModule, HttpClientTestingModule],
      declarations: [ DashboardListComponent ],
      providers: [ DashboardListService ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardListComponent);
    component = fixture.componentInstance;
    dashboardListService = TestBed.get(DashboardListService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should create HttpHeaders', () => {
    const headerParams = component.paramBuilder(1, '10');
    expect(headerParams.get('size')).toBe('10');
    expect(headerParams.get('page')).toBe('1');
    expect(headerParams.get('search')).toBe('');
    expect(headerParams.get('type')).toBe('');
  });
  it('should call getMyDashboards with params', () => {
    const headerParams = component.paramBuilder(1, '10');
    const spy = spyOn(dashboardListService, 'getMyDashboards').and.returnValue({ subscribe: () => {} });
    component.findMyDashboards(headerParams);
    expect(spy).toHaveBeenCalledWith(headerParams);
  });
  it('should call getMyDashboards with params', () => {
    const headerParams = component.paramBuilder(1, '10');
    const spy = spyOn(dashboardListService, 'getAllDashboards').and.returnValue({ subscribe: () => {} });
    component.findAllDashboards(headerParams);
    expect(spy).toHaveBeenCalledWith(headerParams);
  });
  it('should call allDashboardPageChange with params', () => {
    const params = { page : 1 , pageSize : '10' } as IPaginationParams;
    const spy = spyOn(dashboardListService, 'getAllDashboards').and.returnValue({ subscribe: () => {} });
    component.getNextPage(params, false);
    const headerParams = component.paramBuilder(0, '10');
    expect(spy).toHaveBeenCalledWith(headerParams);
  });
  it('should call myDashboardPageChange with params', () => {
    const params = { page : 1 , pageSize : '10' } as IPaginationParams;
    const spy = spyOn(dashboardListService, 'getMyDashboards').and.returnValue({ subscribe: () => {} });
    component.getNextPage(params, true);
    const headerParams = component.paramBuilder(0, '10');
    expect(spy).toHaveBeenCalledWith(headerParams);
  });
  it('should call setDashboardType ', () => {
    component.setDashboardType('Team');
    expect(component.dashboardType).toBe('Team');
  });
});
