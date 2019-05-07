import { TemplatesDirective } from './templates.directive';
import {Component} from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

@Component({
  template: '<ng-template appWidget></ng-template>'
})

class TestTemplateDirectiveComponent {}

describe('TemplatesDirective', () => {
  let component: TestTemplateDirectiveComponent;
  let fixture: ComponentFixture<TestTemplateDirectiveComponent>;
  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TestTemplateDirectiveComponent, TemplatesDirective]
    });
    fixture = TestBed.createComponent(TestTemplateDirectiveComponent);
    component = fixture.componentInstance;
  });
  it('should create an instance', () => {
    expect(component).toBeTruthy();
  });
});
