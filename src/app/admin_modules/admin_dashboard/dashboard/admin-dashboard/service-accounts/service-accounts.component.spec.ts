import { async, ComponentFixture, TestBed } from '@angular/core/testing';
// tslint:disable-next-line:max-line-length
import { DashEditComponent } from '../../../../../shared/dash-edit/dash-edit.component';
import { DashTrashComponent } from '../../../../../shared/dash-trash/dash-trash.component';
import { UserDataService } from '../../../../../shared/services/user-data.service';
import {NgbActiveModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {FormBuilder, FormsModule, ReactiveFormsModule} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import {ServiceAccountsComponent} from './service-accounts.component';
// tslint:disable-next-line:max-line-length
import {CreateOrUpdateServiceAccountComponent} from '../modal/create-or-update-service-account/create-or-update-service-account.component';
import {GeneralDeleteComponent} from '../modal/general-delete/general-delete.component';
import {AdminFilterPipe} from '../../../../../shared/pipes/filter.pipe';
import {AdminOrderByPipe} from '../../../../../shared/pipes/order-by.pipe';

/*@NgModule({
  declarations: [ServiceAccountsComponent, DashEditComponent, DashTrashComponent, AdminFilterPipe,
    AdminOrderByPipe, CreateOrUpdateServiceAccountComponent,
    AdminDeleteComponent],
  providers: [UserDataService, NgbModal],
  imports: [FormsModule, CommonModule, ReactiveFormsModule, NgbModule, HttpClientTestingModule],
  exports: [
    AdminOrderByPipe,
    AdminFilterPipe
  ],
  entryComponents: [
    ServiceAccountsComponent,
    CreateOrUpdateServiceAccountComponent,
    AdminDeleteComponent
  ]
})
class TestModule { }*/

describe('ServiceAccountsComponent', () => {
  let component: ServiceAccountsComponent;
  let fixture: ComponentFixture<ServiceAccountsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ServiceAccountsComponent,
        DashTrashComponent,
        DashEditComponent,
        CreateOrUpdateServiceAccountComponent,
        AdminFilterPipe,
        AdminOrderByPipe,
        GeneralDeleteComponent],
      providers: [UserDataService, FormBuilder, NgbActiveModal],
      imports: [FormsModule, NgbModule, CommonModule, ReactiveFormsModule, HttpClientTestingModule]
    })
      .compileComponents();

  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceAccountsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should loadServiceAccounts', () => {
    component.ngOnInit();
  });
});
