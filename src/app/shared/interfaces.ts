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

export interface Chart {
  title: string;
  component: Type<any>;
  data: any;
  xAxisLabel: string;
  yAxisLabel: string;
  colorScheme: any;
}
