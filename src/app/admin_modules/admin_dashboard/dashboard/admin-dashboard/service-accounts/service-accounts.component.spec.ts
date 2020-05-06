import { async, ComponentFixture, TestBed } from '@angular/core/testing';
// tslint:disable-next-line:max-line-length
import { DeleteConfirmModalComponent } from '../../../../../shared/modals/delete-confirm-modal/delete-confirm-modal.component';
import { DashEditComponent } from '../dash-edit/dash-edit.component';
import { DashTrashComponent } from '../dash-trash/dash-trash.component';
import { UserDataService } from '../../../services/user-data.service';
import {NgbActiveModal, NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {FormBuilder, FormsModule, ReactiveFormsModule} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AdminFilterPipe } from '../../../pipes/filter.pipe';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AdminOrderByPipe } from '../../../pipes/order-by.pipe';
import {ServiceAccountsComponent} from './service-accounts.component';
import {NgModule} from '@angular/core';
// tslint:disable-next-line:max-line-length
import {CreateOrUpdateServiceAccountComponent} from '../modal/create-or-update-service-account/create-or-update-service-account.component';

@NgModule({
  declarations: [ServiceAccountsComponent, DashEditComponent, DashTrashComponent, AdminFilterPipe,
    AdminOrderByPipe, CreateOrUpdateServiceAccountComponent,
    DeleteConfirmModalComponent],
  providers: [UserDataService, NgbModal],
  imports: [FormsModule, CommonModule, ReactiveFormsModule, NgbModule, HttpClientTestingModule],
  exports: [
    AdminOrderByPipe,
    AdminFilterPipe
  ],
  entryComponents: [
    ServiceAccountsComponent,
    CreateOrUpdateServiceAccountComponent,
    DeleteConfirmModalComponent
  ]
})
class TestModule { }

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
        DeleteConfirmModalComponent],
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
