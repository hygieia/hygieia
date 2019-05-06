import { Component } from '@angular/core';

import { ChartComponent } from '../chart/chart.component';

@Component({
  selector: 'app-line-chart',
  templateUrl: './line-chart.component.html',
  styleUrls: ['./line-chart.component.scss']
})
export class LineChartComponent extends ChartComponent {
  constructor() {
    super();
  }

  // options
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showLegend = false;
  tooltipDisabled = false;
  showXAxisLabel = false;
  showYAxisLabel = false;
  trimYAxisTicks = false;
  timeline = false;
  yAxisTickFormatting: (val: number) => string = this.formatInteger;


  formatInteger(val: number): string {
    if (Number.isInteger(val)) {
      return val.toFixed(0);
    }
    return '';
  }
}
