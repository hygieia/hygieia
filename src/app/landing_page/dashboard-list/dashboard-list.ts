export interface IDashboards {
  id: string;
  type: string;
  title: string;
  configurationItemBusServName: string;
  configurationItemBusAppName: string;
}
export interface IDashboardsResponse {
  data: any[];
  total: string;
}
export interface IDashboardsParams {
  page: string;
  size: string;
  search: string;
  type: string;
}
