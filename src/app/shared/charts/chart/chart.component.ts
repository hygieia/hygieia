import { Component, OnInit, Input } from '@angular/core';

@Component({
    template: '',
    styleUrls: ['./chart.component.scss']
})
export class ChartComponent implements OnInit {

    data: any;
    xAxisLabel: string;
    yAxisLabel: string;

    colorScheme: any;

    constructor() { }

    ngOnInit() {
    }

}
