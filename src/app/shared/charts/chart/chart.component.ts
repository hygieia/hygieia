import { Component, Input, OnInit } from '@angular/core';

@Component({
  template: '',
  styleUrls: ['./chart.component.scss']
})
export class ChartComponent implements OnInit {
  @Input() view: any[];
  @Input() title: any;
  @Input() data: any;
  xAxisLabel: string;
  yAxisLabel: string;
  scaleFactor: number;

  colorScheme: any;

  constructor() { }

  ngOnInit() {
  }

}
