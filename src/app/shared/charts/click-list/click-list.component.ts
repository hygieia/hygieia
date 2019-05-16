import { Component, ViewEncapsulation } from '@angular/core';

import { ChartComponent } from '../chart/chart.component';

@Component({
  selector: 'app-click-list',
  templateUrl: './click-list.component.html',
  styleUrls: ['./click-list.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ClickListComponent extends ChartComponent {

}
