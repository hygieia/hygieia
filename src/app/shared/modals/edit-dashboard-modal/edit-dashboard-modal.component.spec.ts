import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { EditDashboardModalComponent } from './edit-dashboard-modal.component';
import { FormBuilder, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { NgbActiveModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { DashboardDataService } from 'src/app/admin_modules/admin_dashboard/services/dashboard-data.service';
import { CmdbDataService } from 'src/app/admin_modules/admin_dashboard/services/cmdb-data.service';
import { AdminDashboardService } from 'src/app/admin_modules/admin_dashboard/services/dashboard.service';
import { PaginationWrapperService } from 'src/app/admin_modules/admin_dashboard/services/pagination-wrapper.service';
import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { UserDataService } from 'src/app/admin_modules/admin_dashboard/services/user-data.service';
import { USER_LIST, DASHBOARDITEM  } from 'src/app/admin_modules/admin_dashboard/services/user-data.service.mockdata';
import {GeneralFilterPipe} from '../../pipes/filter.pipe';
import {GeneralOrderByPipe} from '../../pipes/order-by.pipe';
import {DashTrashComponent} from '../../dash-trash/dash-trash.component';

describe('EditDashboardModalComponent', () => {
  let component: EditDashboardModalComponent;
  let fixture: ComponentFixture<EditDashboardModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EditDashboardModalComponent, GeneralFilterPipe, GeneralOrderByPipe, DashTrashComponent],
      providers: [DashboardDataService,
        CmdbDataService,
        AdminDashboardService,
        PaginationWrapperService, FormBuilder, NgbActiveModal, UserDataService],
      imports: [ReactiveFormsModule, NgbModule, FormsModule, CommonModule, HttpClientTestingModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditDashboardModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should  submit edit from  when clicked on submit', () => {
    setTimeout(() => {
      component.cdfForm.get('dashboardTitle').setValue('Test111');
      component.tabView = 'Dashboard Title';
      component.saveForm();
      expect(component.cdfForm.valid).toBeTruthy();
    }, 500);
  });

  it('should  submit edit form BusinessService  when clicked on submit', () => {
    setTimeout(() => {
      component.formBusinessService.get('configurationItemBusServ').setValue('test111');
      component.formBusinessService.get('configurationItemBusApp').setValue('test111');
      component.tabView = 'Business Service/ Application';
      component.saveForm();
      expect(component.formBusinessService.valid).toBeTruthy();
    }, 500);
  });

  it('should demote user to admin when clicked', () => {
    component.owners = [USER_LIST[0], USER_LIST[1]];
    component.users = USER_LIST;
    component.demoteUserFromOwner(USER_LIST[0]);
    expect(1).toEqual(component.owners.length);
  });

  it('should promote user to admin when clicked', () => {
    component.owners = [];
    component.users = USER_LIST;
    component.promoteUserToOwner(USER_LIST[0]);
    expect(1).toEqual(component.owners.length);
  });

  it('should is ValidBusServName', () => {
    component.dashboardItem = DASHBOARDITEM;
    expect(component.isValidBusServName()).toBeFalsy();
  });

  it('should is ValidBusAppName', () => {
    component.dashboardItem = DASHBOARDITEM;
    expect(component.isValidBusAppName()).toBeFalsy();
  });

  it('should  Owner Information edit from  when clicked on submit', () => {
    component.owners = [USER_LIST[0]];
    component.error = null;
    component.tabView = 'Owner Information';
    component.saveForm();
    expect(null).toEqual(component.error);
  });

  it('should  Score edit from  when clicked on submit', () => {
    component.scoreSettings.scoreEnabled = true;
    component.scoreSettings.scoreDisplay = 'test';
    component.error = null;
    component.tabView = 'Score';
    component.saveForm();
    expect(null).toEqual(component.error);
  });

  it('should  save Widgets edit from  when clicked on submit', () => {
    component.error = null;
    component.tabView = 'Widget Management';
    component.saveForm();
    expect(null).toEqual(component.error);
  });

  it('should isActiveUser ', () => {
    const user = USER_LIST[0];
    expect(component.isActiveUser(user)).toBeFalsy();
    component.authType = user.authType;
    component.username = user.username;
    expect(component.isActiveUser(user)).toBeTruthy();
  });

  it('should get BusApp Tool Text ', () => {
    expect('A Business Application (BAP) CI is a CI Subtype in the application which supports business function (Top level).')
    .toEqual(component.getBusAppToolText());
  });

  it('should get Bus Ser Tool Text ', () => {
    expect('A top level name which support Business function.')
    .toEqual(component.getBusSerToolText());
  });
});
