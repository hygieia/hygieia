import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import {UserDataService} from '../../../../services/user-data.service';
import {NgbActiveModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {FormBuilder, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {CreateOrUpdateServiceAccountComponent} from './create-or-update-service-account.component';

describe('CreateOrUpdateServiceAccountComponent', () => {
  let component: CreateOrUpdateServiceAccountComponent;
  let fixture: ComponentFixture<CreateOrUpdateServiceAccountComponent>;
  const id = '123';
  const fileNames = 'fileNames';
  const serviceAccountName = 'name';

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CreateOrUpdateServiceAccountComponent ],
      providers: [UserDataService, FormBuilder, NgbActiveModal],
      imports: [ReactiveFormsModule, NgbModule, FormsModule, CommonModule, HttpClientTestingModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateOrUpdateServiceAccountComponent);
    component = fixture.componentInstance;
    component.id = id;
    component.fileNames = fileNames;
    component.serviceAccountName = serviceAccountName;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create a from ', () => {
    component.ngOnInit();
    expect(component.serviceAccountForm).toBeTruthy();
  });

  it('should hit submit for edit', () => {
    const submitTest = {
      serviceAccountName: 'name',
      fileNames: 'files'
    };

    setTimeout(() => {
      component.serviceAccountForm.get('serviceAccountName').setValue(submitTest.serviceAccountName);
      component.serviceAccountForm.get('fileNames').setValue(submitTest.fileNames);
      component.id = '123';
      component.submit();
      expect(component.serviceAccountForm.valid).toBeTruthy();
    }, 500);
  });

  it('should hit submit for post', () => {
    const submitTest = {
      serviceAccountName: 'name',
      fileNames: 'files'
    };

    setTimeout(() => {
      component.serviceAccountForm.get('serviceAccountName').setValue(submitTest.serviceAccountName);
      component.serviceAccountForm.get('fileNames').setValue(submitTest.fileNames);
      component.submit();
      expect(component.serviceAccountForm.valid).toBeTruthy();
    }, 500);
  });
});
