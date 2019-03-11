import { Component, OnInit, Input } from '@angular/core';
import { ChartComponent } from '../chart/chart.component';

@Component({
    selector: 'app-line-chart',
    templateUrl: './line-chart.component.html',
    styleUrls: ['./line-chart.component.scss']
})
export class LineChartComponent extends ChartComponent {

    xAxisLabel = 'Country';
    yAxisLabel = 'Population';

    constructor() {
        super();
    }


    // options
    showXAxis = true;
    showYAxis = true;
    gradient = false;
    showLegend = true;
    showXAxisLabel = true;
    showYAxisLabel = true;

    colorScheme = 'vivid';

    onSelect(event) {
        console.log(event);
    }


}
