import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { SharedModule } from '../../shared/shared.module';
import { DashboardListComponent } from './dashboard-list.component';
import { DashboardListService } from './dashboard-list.service';

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
    const params = component.paramBuilder(1, '10');
    expect(params.get('size')).toBe('10');
    expect(params.get('page')).toBe('1');
    expect(params.get('search')).toBe('');
    expect(params.get('type')).toBe('');
  });
  it('should call getMyDashboards with params', () => {
    const params = component.paramBuilder(1, '10');
    const spy = spyOn(dashboardListService, 'getMyDashboards').and.returnValue({ subscribe: () => {} });
    component.findMyDashboards(params);
    expect(spy).toHaveBeenCalledWith(params);
  });
  it('should call getMyDashboards with params', () => {
    const params = component.paramBuilder(1, '10');
    const spy = spyOn(dashboardListService, 'getAllDashboards').and.returnValue({ subscribe: () => {} });
    component.findAllDashboards(params);
    expect(spy).toHaveBeenCalledWith(params);
  });
});
