import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { UpdateJsonComponent } from './update-json.component';
import { UserDataService } from '../../../../services/user-data.service';
import { NgbActiveModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('UpdateJsonComponent', () => {
  let component: UpdateJsonComponent;
  let fixture: ComponentFixture<UpdateJsonComponent>;
  const featureFlagTest = [{
    name: 'test',
    description: 'description',
    flags: { tool: true }
  }];

  const featureFlagNoArray = {
    name: 'test',
    description: 'description',
    flags: { tool: true }
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [UpdateJsonComponent],
      providers: [UserDataService, FormBuilder, NgbActiveModal],
      imports: [ReactiveFormsModule, NgbModule, FormsModule, CommonModule, HttpClientTestingModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateJsonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submitJSON', () => {
    component.jsonForm.get('featureflags').setValue(JSON.stringify(featureFlagTest));
    component.submitJSON();
    expect(component).toBeTruthy();
  });


  it('should submitJSON without an array', () => {
    component.jsonForm.get('featureflags').setValue(JSON.stringify(featureFlagNoArray));
    component.submitJSON();
    expect(component).toBeTruthy();
  });

});
