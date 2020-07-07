import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormModalComponent } from './form-modal.component';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {Directive} from '@angular/core';

@Directive({selector: '[appFormModal]'})
class FormModalStubsDirective { }

describe('FormModalComponent', () => {
  let component: FormModalComponent;
  let fixture: ComponentFixture<FormModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FormModalComponent, FormModalStubsDirective ],
      providers: [ NgbActiveModal ]
    });
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FormModalComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
