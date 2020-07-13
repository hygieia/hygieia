import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GenerateApiTokensComponent } from './generate-api-tokens.component';
import { EditTokenModalComponent } from '../modal/edit-token-modal/edit-token-modal.component';
import { DashEditComponent } from '../dash-edit/dash-edit.component';
import { DashTrashComponent } from '../dash-trash/dash-trash.component';
import { GenerateApiTokenModalComponent } from '../modal/generate-api-token-modal/generate-api-token-modal.component';
import { UserDataService } from '../../../services/user-data.service';
import { NgbModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AdminFilterPipe } from '../../../pipes/filter.pipe';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { API_TOKEN_LIST } from '../../../services/user-data.service.mockdata';
import { NgModule } from '@angular/core';
import { AdminOrderByPipe } from '../../../pipes/order-by.pipe';
import { AdminDeleteComponent } from '../modal/admin-delete/admin-delete.component';

@NgModule({
  declarations: [GenerateApiTokensComponent, DashEditComponent, DashTrashComponent, AdminFilterPipe,
          AdminOrderByPipe, EditTokenModalComponent,
    GenerateApiTokenModalComponent,
    AdminDeleteComponent],
  providers: [UserDataService, NgbModal],
  imports: [FormsModule, CommonModule, ReactiveFormsModule, NgbModule, HttpClientTestingModule],
  entryComponents: [
    EditTokenModalComponent,
    GenerateApiTokenModalComponent,
    AdminDeleteComponent
  ]
})
class TestModule { }

describe('GenerateApiTokensComponent', () => {
  let component: GenerateApiTokensComponent;
  let fixture: ComponentFixture<GenerateApiTokensComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [TestModule],
    })
      .compileComponents();

  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GenerateApiTokensComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should openConfig edit token when clicked', () => {
    component.editToken(API_TOKEN_LIST[0]);
  });

  it('should openConfig delete token when clicked', () => {
    component.deleteToken(API_TOKEN_LIST[0]);
  });

  it('should openConfig generate token when clicked', () => {
    component.generateToken();
  });

});
