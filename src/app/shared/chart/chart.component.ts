import { Component, OnInit, Input } from '@angular/core';

@Component({
    template: '',
    styleUrls: ['./chart.component.scss']
})
export class ChartComponent implements OnInit {

    @Input() data: any;

    constructor() { }

    ngOnInit() {
    }

}
