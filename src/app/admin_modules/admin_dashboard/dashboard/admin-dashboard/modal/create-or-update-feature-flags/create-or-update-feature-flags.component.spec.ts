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
  const id = '123';

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
    component.id = id;
    component.flags = flags;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create a from ', () => {
    component.ngOnInit();
    expect(component.featureFlagForm).toBeTruthy();
  });

  it('should hit flags if statements', () => {
    setTimeout(() => {
      component.ngOnInit();
      expect(component.featureFlagForm).toBeTruthy();
      component.submit();
    }, 500);
  });

  it('should hit flags else statements', () => {
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
    setTimeout(() => {
      component.flags = flags1;
      component.ngOnInit();
      expect(component.featureFlagForm).toBeTruthy();
      component.id = '123';
      component.submit();
    }, 500);
  });
});
