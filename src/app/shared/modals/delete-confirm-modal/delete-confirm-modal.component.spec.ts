import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { DeleteConfirmModalComponent } from './delete-confirm-modal.component';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import { Directive } from '@angular/core';

@Directive({selector: '[appDeleteConfirmModal]'})
class DeleteConfirmModalStubsDirective { }

describe('DeleteConfirmModalComponent', () => {
  let component: DeleteConfirmModalComponent;
  let fixture: ComponentFixture<DeleteConfirmModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DeleteConfirmModalComponent, DeleteConfirmModalStubsDirective ],
      providers: [ NgbActiveModal ]
    });
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeleteConfirmModalComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
