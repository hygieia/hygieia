import { async, ComponentFixture, TestBed } from '@angular/core/testing';
// tslint:disable-next-line:max-line-length
import { DashEditComponent } from '../../../../../shared/dash-edit/dash-edit.component';
import { DashTrashComponent } from '../../../../../shared/dash-trash/dash-trash.component';
import { UserDataService } from '../../../services/user-data.service';
import {NgbActiveModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {FormBuilder, FormsModule, ReactiveFormsModule} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import {ServiceAccountsComponent} from './service-accounts.component';
// tslint:disable-next-line:max-line-length
import {CreateOrUpdateServiceAccountComponent} from '../modal/create-or-update-service-account/create-or-update-service-account.component';
import {GeneralFilterPipe} from '../../../../../shared/pipes/filter.pipe';
import {GeneralOrderByPipe} from '../../../../../shared/pipes/order-by.pipe';
import {GeneralDeleteComponent} from '../../../../../shared/modals/general-delete-modal/general-delete-modal.component';
import {NgxPaginationModule} from 'ngx-pagination';

/*@NgModule({
  declarations: [ServiceAccountsComponent, DashEditComponent, DashTrashComponent, GeneralFilterPipe,
    GeneralOrderByPipe, CreateOrUpdateServiceAccountComponent,
    GeneralDeleteComponent],
  providers: [UserDataService, NgbModal],
  imports: [FormsModule, CommonModule, ReactiveFormsModule, NgbModule, HttpClientTestingModule],
  exports: [
    GeneralOrderByPipe,
    GeneralFilterPipe
  ],
  entryComponents: [
    ServiceAccountsComponent,
    CreateOrUpdateServiceAccountComponent,
    GeneralDeleteComponent
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
        GeneralFilterPipe,
        GeneralOrderByPipe,
        GeneralDeleteComponent],
      providers: [UserDataService, FormBuilder, NgbActiveModal],
      imports: [FormsModule, NgbModule, CommonModule, ReactiveFormsModule, HttpClientTestingModule, NgxPaginationModule]
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
