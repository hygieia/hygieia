import { Type } from '@angular/core';

export interface IUserLogin {
  username: string;
  password: string;
}
export interface IUser {
  sub: string;
  details: string;
  roles: any;
  exp: number;
}
export interface IPaginationParams {
  page: number;
  pageSize: any;
}

export interface IChart {
  title: string;
  component: Type<any>;
  data: any;
  xAxisLabel: string;
  yAxisLabel: string;
  colorScheme: any;
  scaleFactor?: number;
}

export interface IWidget {
  title: string[];
  component: Type<any>[];
  status: string;
  widgetSize: string;
  configForm: Type<any>[];
  deleteForm: Type<any>[];
}
export interface IWidgetConfigResponse {
  widgetConfig: any;
  upsertWidgetResponse?: any;
  deleteWidgetResponse?: any;
}

export interface IAuditResult {
  id: string;
  dashboardId: string;
  dashboardTitle: string;
  lineOfBusiness: string;
  configItemBusServName: string;
  configItemBusAppName: string;
  configItemBusServOwner: string;
  configItemBusAppOwner: string;
  collectorItemId: string;
  auditType: string;
  auditTypeStatus: string;
  auditStatus: string;
  url: string;
  auditDetails: string;
  timestamp: number;
  options: any;
}

export interface IAuditResponsePage {
  content: IAuditResult[];
  totalElements: number;
  totalPages: number;
  last: boolean;
  size: number;
  number: number;
  first: true;
  numberOfElements: number;
}

export interface IAudit {
  lineOfBusiness: string;
  auditType: string;
  auditStatus: string;
  auditTypeStatus: string;
  timestamp: number;
}
