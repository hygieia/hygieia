import { Component, OnInit, Input } from '@angular/core';
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
    view: any[] = [700, 250];
    showXAxis = true;
    showYAxis = true;
    gradient = false;
    showLegend = true;
    showXAxisLabel = true;
    showYAxisLabel = true;
    trimYAxisTicks = false;
    timeline = false;
    yAxisTickFormatting: (val: number) => string = this.formatInteger;

    onSelect(event) {
        console.log(event);
    }

    formatInteger(val: number): string {
        return val.toFixed(0);
    }
}
