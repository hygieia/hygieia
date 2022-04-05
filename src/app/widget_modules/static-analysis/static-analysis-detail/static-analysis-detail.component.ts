import {Component, Input, OnInit} from '@angular/core';
import {
  IClickListItemStaticAnalysis
} from '../../../shared/charts/click-list/click-list-interfaces';

@Component({
  selector: 'app-build-detail',
  templateUrl: './static-analysis-detail.component.html',
  styleUrls: ['./static-analysis-detail.component.scss']
})
export class StaticAnalysisDetailComponent implements OnInit {

  public data: IClickListItemStaticAnalysis;

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
