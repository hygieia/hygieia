import { ConfirmationModalDirective } from './confirmation-modal.directive';
import {Component} from '@angular/core';
import {ComponentFixture, TestBed} from '@angular/core/testing';

@Component({
  template: '<ng-template appModal></ng-template>'
})
class TestConfirmationModalDirectiveComponent {
}

describe('FormModalDirective', () => {
  let component: TestConfirmationModalDirectiveComponent;
  let fixture: ComponentFixture<TestConfirmationModalDirectiveComponent>;

  beforeEach( () => {
    TestBed.configureTestingModule({
      declarations: [TestConfirmationModalDirectiveComponent, ConfirmationModalDirective]
    });
    fixture = TestBed.createComponent(TestConfirmationModalDirectiveComponent);
    component = fixture.componentInstance;

  });
  it('should create an instance', () => {
    expect(component).toBeTruthy();

  });
});
