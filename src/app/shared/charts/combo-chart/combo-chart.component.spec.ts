import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ComboChartComponent } from './combo-chart.component';
import { CommonModule } from '@angular/common';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LineAndBarChartComponent } from '../line-and-bar-chart/line-and-bar-chart.component';
import { NgxUIModule } from '@swimlane/ngx-ui';
import { ComboSeriesVerticalComponent } from '../combo-series-vertical/combo-series-vertical.component';

describe('ComboChartComponent', () => {
  let component: ComboChartComponent;
  let fixture: ComponentFixture<ComboChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ComboChartComponent, LineAndBarChartComponent , ComboSeriesVerticalComponent],
        imports: [CommonModule, NgxChartsModule, BrowserAnimationsModule, NgxUIModule]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ComboChartComponent);
    component = fixture.componentInstance;
    component.colorScheme = 'vivid';
    component.xAxisLabel = 'Test';
    component.yAxisLabel = 'Test';
    component.data = [{}, []];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
