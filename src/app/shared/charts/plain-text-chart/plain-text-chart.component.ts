import { Component, ViewEncapsulation } from '@angular/core';

import { ChartComponent } from '../chart/chart.component';

@Component({
  selector: 'app-plain-text-chart',
  templateUrl: './plain-text-chart.component.html',
  styleUrls: ['./plain-text-chart.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class PlainTextChartComponent extends ChartComponent {

  constructor() {
    super();
  }

}
