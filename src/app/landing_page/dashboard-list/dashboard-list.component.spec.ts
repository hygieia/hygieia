import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { IPaginationParams } from '../../shared/interfaces';
import { SharedModule } from '../../shared/shared.module';
import { DashboardListComponent } from './dashboard-list.component';
import { DashboardListService } from './dashboard-list.service';
import {NbDialogService, NbThemeModule} from '@nebular/theme';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';

class MockDialogService {}

describe('DashboardListComponent', () => {
  let component: DashboardListComponent;
  let fixture: ComponentFixture<DashboardListComponent>;
  let router: Router;
  let dashboardListService;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, SharedModule, NbThemeModule.forRoot(), HttpClientTestingModule,
        RouterTestingModule.withRoutes([]), FormsModule],
      declarations: [ DashboardListComponent ],
      providers: [ DashboardListService, {provide: NbDialogService, useClass: MockDialogService} ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardListComponent);
    component = fixture.componentInstance;
    router = TestBed.get(Router);
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
