import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { EditTokenModalComponent } from './edit-token-modal.component';
import { NgbActiveModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { UserDataService } from 'src/app/admin_modules/admin_dashboard/services/user-data.service';
import { FormBuilder, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { API_TOKEN_LIST } from 'src/app/admin_modules/admin_dashboard/services/user-data.service.mockdata';

describe('EditTokenModalComponent', () => {
  let component: EditTokenModalComponent;
  let fixture: ComponentFixture<EditTokenModalComponent>;
  const apiToken = API_TOKEN_LIST[0];
  const date = {
    day: 20,
    month: 4,
    year: 2020
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EditTokenModalComponent],
      providers: [UserDataService, FormBuilder, NgbActiveModal],
      imports: [ReactiveFormsModule, NgbModule, FormsModule, CommonModule, HttpClientTestingModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditTokenModalComponent);
    component = fixture.componentInstance;
    component.apiUser = apiToken.apiUser;
    component.date = date;
    component.tokenItem = apiToken;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create a edit from ', () => {
    component.ngOnInit();
    expect(component.apiEditForm).toBeTruthy();
  });

  it('should  submit edit from  when clicked on submit', () => {
    setTimeout(() => {
      component.apiEditForm.get('apiUser').setValue('testing1');
      component.submit();
      expect(component.apiEditForm.valid).toBeTruthy();
    }, 500);
  });

});
