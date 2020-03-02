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
  lastUpdated: number;
}

export interface IClickListItemDeploy extends IClickListItem {
  version: string;
  name: string;
}

export interface IClickListData {
  items: IClickListItem[];
  clickableContent: Type<any>;
  clickableHeader: Type<any>;
}
