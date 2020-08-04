import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import {UserDataService} from '../../../../services/user-data.service';
import {NgbActiveModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {FormBuilder, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {CreateOrUpdateApiAuditPropertiesComponent} from './create-or-update-api-audit-properties.component';

describe('CreateOrUpdateApiAuditPropertiesComponent', () => {
  let component: CreateOrUpdateApiAuditPropertiesComponent;
  let fixture: ComponentFixture<CreateOrUpdateApiAuditPropertiesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CreateOrUpdateApiAuditPropertiesComponent ],
      providers: [UserDataService, FormBuilder, NgbActiveModal],
      imports: [ReactiveFormsModule, NgbModule, FormsModule, CommonModule, HttpClientTestingModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateOrUpdateApiAuditPropertiesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    component.name = 'name';
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create a from ', () => {
    component.ngOnInit();
    expect(component.apiAuditPropertiesForm).toBeTruthy();
  });

  it('should hit api audit properties edit statements', () => {
    const submitTest = {
      id: '123',
      name: 'name',
      properties: {test: 'test'}
    };
    setTimeout(() => {
      component.apiAuditPropertiesForm.get('properties').setValue(submitTest.properties);
      component.id = submitTest.id;
      component.submit();
      expect(component.apiAuditPropertiesForm.valid).toBeTruthy();
    }, 500);
  });

  it('should hit api audit properties post statements', () => {
    const submitTest = {
      name: 'name',
      properties: {test: 'test'},
    };
    setTimeout(() => {
      component.apiAuditPropertiesForm.get('name').setValue(submitTest.name);
      component.apiAuditPropertiesForm.get('properties').setValue(submitTest.properties);
      component.submit();
      expect(component.apiAuditPropertiesForm.valid).toBeTruthy();
    }, 500);
  });
});
