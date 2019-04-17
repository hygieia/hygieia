import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChartDirective } from './chart.directive';

@Component({
  template: `<ng-template appChart></ng-template>`
})
class TestChartDirectiveComponent {
}

describe('ChartDirective', () => {
  let component: TestChartDirectiveComponent;
  let fixture: ComponentFixture<TestChartDirectiveComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TestChartDirectiveComponent, ChartDirective]
    });
    fixture = TestBed.createComponent(TestChartDirectiveComponent);
    component = fixture.componentInstance;
  });

  it('should create an instance', () => {
    expect(component).toBeTruthy();
  });
});

