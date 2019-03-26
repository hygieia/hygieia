import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NumberCardChartComponent } from './number-card-chart.component';
import { CommonModule } from '@angular/common';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgxUIModule } from '@swimlane/ngx-ui';

describe('NumberCardChartComponent', () => {
  let component: NumberCardChartComponent;
  let fixture: ComponentFixture<NumberCardChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NumberCardChartComponent ],
        imports: [CommonModule, NgxChartsModule, BrowserAnimationsModule, NgxUIModule]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NumberCardChartComponent);
    component = fixture.componentInstance;
    component.colorScheme = 'vivid';
    component.xAxisLabel = 'Test';
    component.yAxisLabel = 'Test';
    component.data = {};
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
