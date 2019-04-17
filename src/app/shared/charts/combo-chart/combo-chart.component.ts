import { Component } from '@angular/core';

import { ChartComponent } from '../chart/chart.component';

@Component({
  selector: 'app-combo-chart',
  templateUrl: './combo-chart.component.html',
  styleUrls: ['./combo-chart.component.scss']
})
export class ComboChartComponent extends ChartComponent {
  constructor() {
    super();
  }

  // options
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showLegend = false;
  showXAxisLabel = true;
  showYAxisLabel = true;
  trimYAxisTicks = false;
  yAxisTickFormatting: (val: number) => string = this.formatToMinute;

  lineChartScheme = {
    domain: ['gray']
  };

  formatInteger(val: number): string {
    return val.toFixed(0);
  }

  formatToMinute(val: number): string {
    const minutes = Math.floor(val / 60000);
    const seconds = ((val % 60000) / 1000);
    return (seconds === 60 ? (minutes + 1) + ':00' : minutes + ':' + (seconds < 10 ? '0' : '') + seconds.toFixed(0));
  }
}
