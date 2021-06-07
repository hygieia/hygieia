import { Component } from '@angular/core';

import { ChartComponent } from '../chart/chart.component';

@Component({
  selector: 'app-gauge-chart',
  templateUrl: './gauge-chart.component.html',
  styleUrls: ['./gauge-chart.component.scss']
})
export class GaugeChartComponent extends ChartComponent {

  formatting = this.formatAppendPercent;

  constructor() {
    super();
    this.scaleFactor = 1;
  }

  formatAppendPercent(val: number): string {
    return val + '%';
  }

}
