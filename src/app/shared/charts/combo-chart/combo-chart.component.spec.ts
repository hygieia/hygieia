import { CommonModule } from '@angular/common';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { NgxUIModule } from '@swimlane/ngx-ui';

import { MinutesPipe } from '../../pipes/minutes.pipe';
import { ComboSeriesVerticalComponent } from '../combo-series-vertical/combo-series-vertical.component';
import { LineAndBarChartComponent } from '../../ngx-charts/line-and-bar-chart/line-and-bar-chart.component';
import { ComboChartComponent } from './combo-chart.component';
import {ViewRef} from '@angular/core';

describe('ComboChartComponent', () => {
  let component: ComboChartComponent;
  let fixture: ComponentFixture<ComboChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ComboChartComponent, LineAndBarChartComponent, ComboSeriesVerticalComponent, MinutesPipe],
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
    if (!(fixture.changeDetectorRef as ViewRef).destroyed) {
      fixture.detectChanges();
    }
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
