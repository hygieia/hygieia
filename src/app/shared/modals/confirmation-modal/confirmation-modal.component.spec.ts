import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ConfirmationModalComponent } from './confirmation-modal.component';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import { Directive } from '@angular/core';

@Directive({selector: '[appConfirmationModal]'})
class ConfirmationModalStubsDirective { }

describe('ConfirmationModalComponent', () => {
  let component: ConfirmationModalComponent;
  let fixture: ComponentFixture<ConfirmationModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConfirmationModalComponent, ConfirmationModalStubsDirective ],
      providers: [ NgbActiveModal ]
    });
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmationModalComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
