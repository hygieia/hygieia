import { DASHBOARDDATA } from './user-data.service.mockdata';
import { of } from 'rxjs';

export class MockDashboardDataService {
  data = {data: DASHBOARDDATA};
  getPromise(route) {
    return of(this.data);
  }
  search() {
    return of(this.data);
  }
  mydashboard(username) {
    return of(this.data);
  }
  myowner(id) {
    return of(this.data);
  }
  getComponent(componentId) {
    return of(this.data);
  }
  owners(id) {
    return of(this.data);
  }
  updateOwners(id, owners) {
    return of(this.data);
  }
  detail(id) {
    return of(this.data);
  }
  create(data) {
    return of(this.data);
  }
  renameDashboard(id, newDashboardName) {
    return of(this.data);
  }
  deleteDashboard(id) {
    return of(this.data);
  }
  types() {
    return [
      {
        id: 'team',
        name: 'Team'
      },
      {
        id: 'product',
        name: 'Product'
      }
    ];
  }
  upsertWidget(dashboardId, widget) {
    return of(this.data);
  }
  updateBusItems(id, data) {
    return of(this.data);
  }
  updateDashboardWidgets(id, data) {
    return of(this.data);
  }
  deleteWidget(dashboardId, widget) {
    return of(this.data);
  }
  count(type) {
    return of(this.data);
  }
  public searchByPage(params) {
    return of(this.data);
  }
  filterByTitle(params) {
    return of(this.data);
  }
  filterCount(title, type) {
    return of(this.data);
  }
  getPageSize() {
    return of(this.data);
  }
  myDashboardsCount(type) {
    return of(this.data);
  }
  searchMyDashboardsByPage(params) {
    return of(this.data);
  }
  filterMyDashboardsByTitle(params) {
    return of(this.data);
  }
  filterMyDashboardCount(title, type) {
    return of(this.data);
  }
  getGeneralConfig(id) {
    return of(this.data);
  }
  generalConfigSave(obj) {
    return of(this.data);
  }
  updateDashboardScoreSettings(id, scoreEnabled, scoreDisplay) {
    return of(this.data);
}
}
