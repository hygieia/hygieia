import { Type } from '@angular/core';

export enum DashStatus {
  IGNORE,
  IN_PROGRESS,
  PASS,
  WARN,
  FAIL,
  UNAUTH,
  CRITICAL
}

export interface IClickListData {
  items: IClickListItem[];
  clickableContent: Type<any>;
  clickableHeader: Type<any>;
}

export interface IClickListItem {
  status: DashStatus;
  statusText: string;
  title: string;
  subtitles: any[];
  url: string;
}

export interface IClickListItemDeploy extends IClickListItem {
  version: string;
  name: string;
  url: string;
  lastUpdated: number;
  regex: string;
}

export interface IClickListItemFeature extends IClickListItem {
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
  type: string;
  time: string;
  changeDate: string;
}

export interface IClickListItemStaticAnalysis extends IClickListData {
  url: string;
  version?: string;
  name?: string;
  timestamp?: any;
}
