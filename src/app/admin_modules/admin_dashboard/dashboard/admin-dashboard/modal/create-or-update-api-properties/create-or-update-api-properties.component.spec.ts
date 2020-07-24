import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import {UserDataService} from '../../../../services/user-data.service';
import {NgbActiveModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {FormBuilder, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {CreateOrUpdateApiPropertiesComponent} from './create-or-update-api-properties.component';

describe('CreateOrUpdateApiPropertiesComponent', () => {
  let component: CreateOrUpdateApiPropertiesComponent;
  let fixture: ComponentFixture<CreateOrUpdateApiPropertiesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CreateOrUpdateApiPropertiesComponent ],
      providers: [UserDataService, FormBuilder, NgbActiveModal],
      imports: [ReactiveFormsModule, NgbModule, FormsModule, CommonModule, HttpClientTestingModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateOrUpdateApiPropertiesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    component.name = 'name';
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create a from ', () => {
    component.ngOnInit();
    expect(component.apiPropertiesForm).toBeTruthy();
  });

  it('should hit api properties edit statements', () => {
    const submitTest = {
      id: '123',
      name: 'name',
      properties: {test: 'test'}
    };
    setTimeout(() => {
      component.apiPropertiesForm.get('properties').setValue(submitTest.properties);
      component.id = submitTest.id;
      component.submit();
      expect(component.apiPropertiesForm.valid).toBeTruthy();
    }, 500);
  });

  it('should hit api properties post statements', () => {
    const submitTest = {
      name: 'name',
      properties: {test: 'test'},
    };
    setTimeout(() => {
      component.apiPropertiesForm.get('name').setValue(submitTest.name);
      component.apiPropertiesForm.get('properties').setValue(submitTest.properties);
      component.submit();
      expect(component.apiPropertiesForm.valid).toBeTruthy();
    }, 500);
  });
});
