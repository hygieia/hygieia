import { Type } from '@angular/core';

export interface ILineChartData {
  areaChart: boolean;
  detailComponent: Type<any>;
  dataPoints: any;
}

export interface ILineChartRepoItem extends ILineChartData {
  number: string;
  author: string;
  message: string;
  time: string;
  date: string;
}
