import { Component, ViewEncapsulation } from '@angular/core';

import { ChartComponent } from '../chart/chart.component';

@Component({
  selector: 'app-combo-chart',
  templateUrl: './combo-chart.component.html',
  styleUrls: ['./combo-chart.component.scss'],
  encapsulation: ViewEncapsulation.None
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
  showXAxisLabel = false;
  showYAxisLabel = false;
  trimYAxisTicks = false;
  yAxisTickFormatting: (val: number) => string = this.formatToMinute;
  xAxisTickFormatting: (val: Date) => string = this.formatToDayAndMonth;

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

  formatToDayAndMonth(val: Date): string {
    return (val.getMonth() + 1) + '/' + val.getDate();
  }
}
