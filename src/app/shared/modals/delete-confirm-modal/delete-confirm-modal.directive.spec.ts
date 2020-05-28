import { DeleteConfirmModalDirective } from './delete-confirm-modal.directive';
import {Component} from '@angular/core';
import {ComponentFixture, TestBed} from '@angular/core/testing';

@Component({
  template: '<ng-template appModal></ng-template>'
})
class TestDeleteConfirmModalDirectiveComponent {
}

describe('FormModalDirective', () => {
  let component: TestDeleteConfirmModalDirectiveComponent;
  let fixture: ComponentFixture<TestDeleteConfirmModalDirectiveComponent>;

  beforeEach( () => {
    TestBed.configureTestingModule({
      declarations: [TestDeleteConfirmModalDirectiveComponent, DeleteConfirmModalDirective]
    });
    fixture = TestBed.createComponent(TestDeleteConfirmModalDirectiveComponent);
    component = fixture.componentInstance;

  });
  it('should create an instance', () => {
    expect(component).toBeTruthy();

  });
});
