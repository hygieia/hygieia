import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NumberCardChartComponent } from './number-card-chart.component';

describe('NumberCardChartComponent', () => {
  let component: NumberCardChartComponent;
  let fixture: ComponentFixture<NumberCardChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NumberCardChartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NumberCardChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
