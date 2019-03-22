import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LineAndBarChartComponent } from './line-and-bar-chart.component';

describe('LineAndBarChartComponent', () => {
  let component: LineAndBarChartComponent;
  let fixture: ComponentFixture<LineAndBarChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LineAndBarChartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LineAndBarChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
