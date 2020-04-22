import {Component, ViewEncapsulation} from '@angular/core';

import { ChartComponent } from '../chart/chart.component';

@Component({
  selector: 'app-pie-grid-chart',
  templateUrl: './pie-grid-chart.component.html',
  styleUrls: ['./pie-grid-chart.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class PieGridChartComponent extends ChartComponent {

  // options here

  constructor() {
    super();
  }

}
