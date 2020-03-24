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
}

export interface IClickListItemDeploy extends IClickListItem {
  version: string;
  name: string;
  url: string;
  lastUpdated: number;
  regex: string;
}
