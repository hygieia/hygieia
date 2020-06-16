import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import {NgbActiveModal, NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {FormBuilder, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {FeatureFlagsComponent} from './feature-flags.component';
import {UserDataService} from '../../../services/user-data.service';
import {DashTrashComponent} from '../dash-trash/dash-trash.component';
import {DashEditComponent} from '../dash-edit/dash-edit.component';
import {CreateOrUpdateFeatureFlagsComponent} from '../modal/create-or-update-feature-flags/create-or-update-feature-flags.component';
import {NgModule} from '@angular/core';
import { AdminDeleteComponent } from '../modal/admin-delete/admin-delete.component';

@NgModule({
  declarations: [FeatureFlagsComponent, DashEditComponent, DashTrashComponent, AdminDeleteComponent],
  providers: [UserDataService, NgbModal],
  imports: [FormsModule, CommonModule, ReactiveFormsModule, NgbModule, HttpClientTestingModule],
  entryComponents: [
    FeatureFlagsComponent,
    AdminDeleteComponent,
    CreateOrUpdateFeatureFlagsComponent
  ]
})
class TestModule { }

describe('FeatureFlagsComponent', () => {
  let component: FeatureFlagsComponent;
  let fixture: ComponentFixture<FeatureFlagsComponent>;
  const id = '123';
  const name = 'name';
  const description = 'description';
  const flags = {
    agileTool: false,
    artifact: false,
    build: false,
    codeQuality: false,
    deployment: false,
    libraryPolicy: false,
    scm: false,
    staticSecurityScan: false,
    test: false,
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FeatureFlagsComponent, DashTrashComponent, DashEditComponent, CreateOrUpdateFeatureFlagsComponent,
        AdminDeleteComponent],
      providers: [UserDataService, FormBuilder, NgbActiveModal],
      imports: [ReactiveFormsModule, NgbModule, FormsModule, CommonModule, HttpClientTestingModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FeatureFlagsComponent);
    component = fixture.componentInstance;
    component.name = name;
    component.id = id;
    component.description = description;
    component.flags = flags;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should hit flagKeys', () => {
    component.flagKeys({test: 'test'});
  });
});
