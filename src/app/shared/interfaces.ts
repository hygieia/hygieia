
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

export  interface IDashboard  {
  widgets: string;

}
