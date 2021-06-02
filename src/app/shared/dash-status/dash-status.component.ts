import { Component, Input, OnInit } from '@angular/core';

import { DashStatus } from './DashStatus';

@Component({
  selector: 'app-dash-status',
  templateUrl: './dash-status.component.html',
  styleUrls: ['./dash-status.component.scss']
})
export class DashStatusComponent implements OnInit {

  DashStatus: typeof DashStatus = DashStatus;
  @Input() statusText: string;
  @Input() status: DashStatus;

  constructor() { }

  ngOnInit() {
  }

}
