import { Component, Input, OnInit } from '@angular/core';

@Component({
  template: '',
  styleUrls: ['./chart.component.scss']
})
export class ChartComponent implements OnInit {

  @Input() view: any[];
  data: any;
  xAxisLabel: string;
  yAxisLabel: string;

  colorScheme: any;

  constructor() { }

  ngOnInit() {
  }

}
