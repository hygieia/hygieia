import { WidgetDirective } from './widget.directive';
import {Component} from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

@Component({
  template: `<ng-template appWidget></ng-template>`
})

class TestWidgetDirectiveComponent {}


describe('WidgetDirective', () => {
  let component: TestWidgetDirectiveComponent;
  let fixture: ComponentFixture<TestWidgetDirectiveComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TestWidgetDirectiveComponent, WidgetDirective]
    });
    fixture = TestBed.createComponent(TestWidgetDirectiveComponent);
    component = fixture.componentInstance;
  });
  it('should create an instance', () => {
    expect(component).toBeTruthy();
  });
});
