import { FormModalDirective } from './form-modal.directive';
import {Component} from '@angular/core';
import {ComponentFixture, TestBed} from '@angular/core/testing';

@Component({
  template: '<ng-template appModal></ng-template>'
})
class TestFormModalDirectiveComponent {
}

describe('FormModalDirective', () => {
  let component: TestFormModalDirectiveComponent;
  let fixture: ComponentFixture<TestFormModalDirectiveComponent>;

  beforeEach( () => {
    TestBed.configureTestingModule({
      declarations: [TestFormModalDirectiveComponent, FormModalDirective]
    });
    fixture = TestBed.createComponent(TestFormModalDirectiveComponent);
    component = fixture.componentInstance;

  });
  it('should create an instance', () => {
    expect(component).toBeTruthy();

  });
});
