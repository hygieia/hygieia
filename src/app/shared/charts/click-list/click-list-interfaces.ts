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

export interface IClickListItem {
  status: DashStatus;
  statusText: string;
  title: string;
  subtitles: any[];
  url: string;
}

export interface IClickListData {
  items: IClickListItem[];
  clickableContent: Type<any>;
  clickableHeader: Type<any>;
  title: string;
  version: string;
  name: string;
  lastUpdated: string;
  obj: any;
}
