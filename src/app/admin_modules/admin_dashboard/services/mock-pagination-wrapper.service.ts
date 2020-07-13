import { DASHBOARDDATA, DASHBOARDDATARESPONSE } from './user-data.service.mockdata';

export class MockPaginationWrapperService {
      data = {data: DASHBOARDDATA};
      responseData = DASHBOARDDATARESPONSE;
  calculateTotalItems(type) {
        return{ subscribe: (callBack) => callBack(this.data) };
  }
  calculateTotalItemsMyDash(type) {
        return{ subscribe: (callBack) => callBack(this.data) };
  }
  getTotalItems() {
        return 200;
  }
  getTotalItemsMyDash() {
        return{ subscribe: (callBack) => callBack(this.data) };
  }
  getCurrentPage() {
        return{ subscribe: (callBack) => callBack(this.data) };
  }
  getPageSize() {
        return{ subscribe: (callBack) => callBack(this.data) };
  }
  getDashboards() {
        return{ subscribe: (callBack) => callBack(this.data) };
  }
  getMyDashboards() {
        return{ subscribe: (callBack) => callBack(this.data) };
  }
  setDashboards(paramDashboards) {
        return{ subscribe: (callBack) => callBack(this.data) };
  }
  getInvalidAppOrCompError(data) {
        return{ subscribe: (callBack) => callBack(this.data) };
  }
  pageChangeHandler(pageNumber, type) {
        return{ subscribe: (callBack) => callBack(this.data) };
  }
  pageChangeHandlerForMyDash = (pageNumber, type) => {
        return{ subscribe: (callBack) => callBack(this.data) };
  }
  public processDashboardResponse = (response) => {
        return  this.responseData;
  }
  public processDashboardFilterResponse = (response) => {
        return{ subscribe: (callBack) => callBack(this.data) };
  }
  public processDashboardError = (data) => {
        return{ subscribe: (callBack) => callBack(this.data) };
  }
  public processMyDashboardResponse = (response) => {
        return{ subscribe: (callBack) => callBack(this.data) };
  }
  public processFilterMyDashboardResponse = (response) => {
        return{ subscribe: (callBack) => callBack(this.data) };
  }
  public processMyDashboardError = (data) => {
        return{ subscribe: (callBack) => callBack(this.data) };
  }
  filterByTitle = (title, type) => {
        return{ subscribe: (callBack) => callBack(this.data) };
  }
}



