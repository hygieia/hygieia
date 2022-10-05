import { CommonModule } from '@angular/common';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgxChartsModule } from '@swimlane/ngx-charts';
// import { NgxUIModule } from '@swimlane/ngx-ui';

import { MinutesPipe } from '../../pipes/minutes.pipe';
import { ComboSeriesVerticalComponent } from '../combo-series-vertical/combo-series-vertical.component';
import { LineAndBarChartComponent } from '../../ngx-charts/line-and-bar-chart/line-and-bar-chart.component';
import { ComboChartComponent } from './combo-chart.component';
import { ChartComponent } from '../chart/chart.component';
import { BaseChartComponent } from '@swimlane/ngx-charts';


describe('ComboChartComponent', () => {
  let component: ComboChartComponent;
  let fixture: ComponentFixture<ComboChartComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ComboChartComponent, ChartComponent, LineAndBarChartComponent,
        ComboSeriesVerticalComponent, MinutesPipe, BaseChartComponent],
      imports: [CommonModule, NgxChartsModule, BrowserAnimationsModule,
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ComboChartComponent);
    component = fixture.componentInstance;
    component.colorScheme = 'vivid';
    component.xAxisLabel = 'Test';
    component.yAxisLabel = 'Test';
    component.data = {};
    // fixture.detectChanges();
  });
  // Broken - says this.lineChart is not iterable
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should format integer', () => {
    expect(component.formatInteger(12312.323)).toEqual('12312');
  });

  it('should format to minute', () => {
    expect(component.formatToMinute(12312)).toEqual('0:12');
  });

  it('should format to day and month', () => {
    expect(component.formatToDayAndMonth(new Date('Tue Feb 05 2019 12:05:22 GMT+0530 (IST)'))).toEqual('2/5');
  });
});