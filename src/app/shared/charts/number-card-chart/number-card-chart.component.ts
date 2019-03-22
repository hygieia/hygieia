import { Component, OnInit } from '@angular/core';
import { ChartComponent } from '../chart/chart.component';

@Component({
  selector: 'app-number-card-chart',
  templateUrl: './number-card-chart.component.html',
  styleUrls: ['./number-card-chart.component.scss']
})
export class NumberCardChartComponent extends ChartComponent {

    constructor() {
        super();
    }

    // options
    view: any[] = [500, 250];
    textColor = 'white';
    valueFormatting: (val: number) => string = this.formatInteger;


    formatInteger(val: number): string {
        return val.toFixed(0);
    }
}
