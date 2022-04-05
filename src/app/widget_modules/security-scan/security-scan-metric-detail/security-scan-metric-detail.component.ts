import {Component, Input, OnInit } from '@angular/core';
import { IClickListItemMetric } from 'src/app/shared/charts/click-list/click-list-interfaces';

@Component({
  selector: 'app-security-scan-metric-detail',
  templateUrl: './security-scan-metric-detail.component.html',
  styleUrls: ['./security-scan-metric-detail.component.scss']
})
export class SecurityScanMetricDetailComponent implements OnInit {

  public data: IClickListItemMetric;
  displayedColumns = ['severity', 'status', 'updated', 'path'];


  constructor() {}

  ngOnInit() {
  }

  // click list header details
  @Input()
  set detailData(data: any) {
    this.data = data;
  }

  isDate(obj): boolean {
    return obj instanceof Date;
  }

  formatDate(date: Date) {
    return new Date(date).toUTCString();
  }

}
