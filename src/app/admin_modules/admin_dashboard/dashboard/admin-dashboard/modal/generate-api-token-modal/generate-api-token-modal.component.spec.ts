import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NgModule } from '@angular/core';
import {GenerateApiTokensComponent} from '../../generate-api-tokens/generate-api-tokens.component';
import {DashEditComponent} from '../../../../../../shared/dash-edit/dash-edit.component';
import {GenerateApiTokenModalComponent} from './generate-api-token-modal.component';
import {AdminOrderByPipe} from '../../../../../../shared/pipes/order-by.pipe';
import {EditTokenModalComponent} from '../edit-token-modal/edit-token-modal.component';
import {GeneralDeleteComponent} from '../../../../../../shared/modals/general-delete/general-delete.component';
import {DashTrashComponent} from '../../../../../../shared/dash-trash/dash-trash.component';
import {AdminFilterPipe} from '../../../../../../shared/pipes/filter.pipe';
import {UserDataService} from '../../../../../../shared/services/user-data.service';
import {API_TOKEN_LIST} from '../../../../../../shared/services/user-data.service.mockdata';

@NgModule({
  declarations: [GenerateApiTokensComponent, DashEditComponent, DashTrashComponent, AdminFilterPipe,
    AdminOrderByPipe, EditTokenModalComponent,
    GenerateApiTokenModalComponent,
    GeneralDeleteComponent],
  providers: [UserDataService, NgbModal],
  imports: [FormsModule, CommonModule, ReactiveFormsModule, NgbModule, HttpClientTestingModule],
  entryComponents: [
    EditTokenModalComponent,
    GenerateApiTokenModalComponent,
    GeneralDeleteComponent
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
