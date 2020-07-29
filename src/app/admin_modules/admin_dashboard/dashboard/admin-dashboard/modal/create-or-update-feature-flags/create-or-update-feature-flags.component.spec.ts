import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CreateOrUpdateFeatureFlagsComponent } from './create-or-update-feature-flags.component';
import {UserDataService} from '../../../../services/user-data.service';
import {NgbActiveModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {FormBuilder, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('CreateOrUpdateFeatureFlagsComponent', () => {
  let component: CreateOrUpdateFeatureFlagsComponent;
  let fixture: ComponentFixture<CreateOrUpdateFeatureFlagsComponent>;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CreateOrUpdateFeatureFlagsComponent ],
      providers: [UserDataService, FormBuilder, NgbActiveModal],
      imports: [ReactiveFormsModule, NgbModule, FormsModule, CommonModule, HttpClientTestingModule]
    })
      .compileComponents();
  }));
  beforeEach(() => {
    fixture = TestBed.createComponent(CreateOrUpdateFeatureFlagsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });
  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should create a from ', () => {
    component.ngOnInit();
    expect(component.featureFlagForm).toBeTruthy();
  });
  it('should hit flags edit statements', () => {
    const flags = {
      agileTool: 'false',
      artifact: 'false',
      build: 'false',
      codeQuality: 'false',
      deployment: 'false',
      libraryPolicy: 'false',
      scm: 'false',
      staticSecurityScan: 'false',
      test: 'false',
    };
    const submitTest = {
      name: 'name',
      description: 'description',
      flags
    };
    setTimeout(() => {
      component.featureFlagForm.get('name').setValue(submitTest.name);
      component.featureFlagForm.get('description').setValue(submitTest.description);
      component.id = '123';
      component.submit();
      expect(component.featureFlagForm.valid).toBeTruthy();
    }, 500);
  });
  it('should hit flags post statements', () => {
    const flags1 = {
      agileTool: undefined,
      artifact: undefined,
      build: undefined,
      codeQuality: undefined,
      deployment: undefined,
      libraryPolicy: undefined,
      scm: undefined,
      staticSecurityScan: undefined,
      test: undefined,
    };
    const submitTest = {
      name: 'name',
      description: 'description',
      flags: flags1
    };
    setTimeout(() => {
      component.featureFlagForm.get('name').setValue(submitTest.name);
      component.featureFlagForm.get('description').setValue(submitTest.description);
      component.submit();
      expect(component.featureFlagForm.valid).toBeTruthy();
    }, 500);
  });
});
