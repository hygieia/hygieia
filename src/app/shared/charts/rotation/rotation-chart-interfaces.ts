import { Type } from '@angular/core';

export interface IRotationData {
  items: IRotationItem[][];
  clickableContent: Type<any>;
  clickableHeader: Type<any>;
}

export interface IRotationItem {
  type: string;
  title: string;
  subtitles: any[];
  rotationData: any[];
}

export interface IFeatureRotationItem extends IRotationItem {
  agileType: string;
  sEpicName: string;
  sEpicUrl: string;
  sEstimate: string;
  sEpicNumber: string;
  sNumber: string;
  sEstimateTime: string;
  sName: string;
  sStatus: string;
  sUrl: string;
  progressStatus: string;
  name: string;
  url: string;
  number: string;
  collectionType: string;
  date: string;
  time: string;
  changeDate: string;
}
