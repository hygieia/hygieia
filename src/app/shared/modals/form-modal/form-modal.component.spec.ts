import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NgModule } from '@angular/core';
import { FormModalComponent } from './form-modal.component';
import {NgbActiveModal, NgbTypeahead, NgbTypeaheadModule} from '@ng-bootstrap/ng-bootstrap';
import {Component, ComponentFactoryResolver, CUSTOM_ELEMENTS_SCHEMA, Directive, NO_ERRORS_SCHEMA} from '@angular/core';
import {BuildConfigFormComponent} from '../../../widget_modules/build/build-config-form/build-config-form.component';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {FormBuilder, FormGroup, NgForm, ReactiveFormsModule} from '@angular/forms';
import {FormModalDirective} from './form-modal.directive';

// @Directive({selector: '[appFormModal]'})
// class FormModalStubDirective { }

// @NgModule({
//   declarations: [ BuildConfigFormComponent],
//   entryComponents: [ BuildConfigFormComponent ]
// })
// class TestModule { }

@Directive({selector: '[appFormModal]'})
class FormModalStubsDirective { }

describe('FormModalComponent', () => {
  let component: FormModalComponent;
  let fixture: ComponentFixture<FormModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      // declarations: [ FormModalComponent, FormModalDirective ],
      declarations: [ FormModalComponent, FormModalStubsDirective ],
      providers: [ NgbActiveModal ]
    });

    // TestBed.overrideModule(BrowserDynamicTestingModule, {
    //   set: {
    //     entryComponents: [ BuildConfigFormComponent ]
    //   }
    // });
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FormModalComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
