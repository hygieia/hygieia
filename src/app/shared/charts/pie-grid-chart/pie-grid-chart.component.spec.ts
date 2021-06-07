import { CommonModule } from '@angular/common';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { NgxUIModule } from '@swimlane/ngx-ui';

import { PieGridChartComponent } from './pie-grid-chart.component';
import {PieGridComponent} from '../../ngx-charts/pie-grid/pie-grid.component';

describe('PieGridChartComponent', () => {
  let component: PieGridChartComponent;
  let fixture: ComponentFixture<PieGridChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PieGridChartComponent, PieGridComponent],
      imports: [CommonModule, NgxChartsModule, BrowserAnimationsModule, NgxUIModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PieGridChartComponent);
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
