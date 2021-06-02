import { Component, ViewEncapsulation } from '@angular/core';

import { ChartComponent } from '../chart/chart.component';

@Component({
  selector: 'app-horizontal-bar-chart',
  templateUrl: './horizontal-bar-chart.component.html',
  styleUrls: ['./horizontal-bar-chart.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class HorizontalBarChartComponent extends ChartComponent {
  constructor() {
    super();
  }

  // options
  showXAxis = true;
  showYAxis = true;
  showLegend = false;
  showXAxisLabel = true;
  showYAxisLabel = true;
  gradient = true;
  trimXAxisTicks = true;
  trimYAxisTicks = true;
  noBarWhenZero = false;

  onSelect($event: Event) {

  }

  onActivate($event: any) {

  }

  onDeactivate($event: any) {

  }

}
