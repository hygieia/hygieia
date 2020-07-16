import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, catchError } from 'rxjs/operators';
import { clone } from 'lodash';

@Injectable({
  providedIn: 'root'
})
export class DashboardDataService {

  HygieiaConfig: any = { local: null };


  testSearchRoute = 'test-data/dashboard_search.json';
  testDetailRoute = 'test-data/dashboard_detail.json';
  testOwnedRoute = 'test-data/dashboard_owned.json';
  testAllUsersRoute = 'test-data/all_users.json';
  testOwnersRoute = 'test-data/owners.json';
  dashboardRoute = '/api/dashboard';
  mydashboardRoute = '/api/dashboard/mydashboard';
  myownerRoute = '/api/dashboard/myowner';
  updateBusItemsRoute = '/api/dashboard/updateBusItems';
  updateDashboardWidgetsRoute = '/api/dashboard/updateDashboardWidgets';
  dashboardRoutePage = '/api/dashboard/page';
  dashboardFilterRoutePage = '/api/dashboard/page/filter';
  dashboardCountRoute = '/api/dashboard/count';
  dashboardFilterCountRoute = '/api/dashboard/filter/count';
  dashboardPageSize = '/api/dashboard/pagesize';
  myDashboardRoutePage = '/api/dashboard/mydashboard/page';
  myDashboardFilterRoutePage = '/api/dashboard/mydashboard/page/filter';
  myDashboardCountRoute = '/api/dashboard/mydashboard/count';
  myDashboardFilterCountRoute = '/api/dashboard/mydashboard/filter/count';
  dashboardGenconfigRoute = '/api/dashboard/generalConfig';
  updateDashboardScoreSettingsRoute = '/api/dashboard/updateScoreSettings';
  myComponentRoute = '/api/dashboard/myComponent';
  mywigets = '/api/dashboard/template';

  constructor(private http: HttpClient) {
  }

  // reusable helper
  getPromise(route) {
    return this.http.get(route)
      .pipe(map((response: any) => {
        return response;
      }));
  }

  // gets list of dashboards
  search() {
    return this.getPromise(this.HygieiaConfig.local ? this.testSearchRoute : this.dashboardRoute);
  }

  // gets list of owned dashboard
  mydashboard(username) {
    return this.getPromise(this.HygieiaConfig.local ? this.testOwnedRoute : this.mydashboardRoute + '/?username=' + username);
  }

  // gets dashboard owner from dashboard title
  myowner(id) {
    return this.getPromise(this.HygieiaConfig.local ? this.testOwnedRoute : this.myownerRoute + '/' + id);
  }

  // gets component from componentId
  getComponent(componentId) {
    return this.getPromise(this.HygieiaConfig.local ? this.testOwnedRoute : this.myComponentRoute + '/' + componentId);
  }

  owners(id) {
    return this.getPromise(this.HygieiaConfig.local ? this.testOwnersRoute : this.dashboardRoute + '/' + id + '/owners');
  }

  updateOwners(id, owners) {
    return this.http.put(this.dashboardRoute + '/' + id + '/owners', owners);
  }

  // gets info for a single dashboard including available widgets
  detail(id) {
    return this.getPromise(this.HygieiaConfig.local ? this.testDetailRoute : this.dashboardRoute + '/' + id);
  }

  // creates a new dashboard
  create(data) {
    return this.http.post(this.dashboardRoute, data)
      .subscribe((response: any) => {
        return response;
      },
        (error) => {
          return null;
        });
  }

  // renames a dashboard

  renameDashboard(id, newDashboardName) {
    const postData = {
      title: newDashboardName
    };
    return this.http.put(this.dashboardRoute + '/rename/' + id, postData);
  }

  // delete a dashboard
  deleteDashboard(id) {
    return this.http.delete(this.dashboardRoute + '/' + id);
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

  getMyWidget(template) {
    return this.http.get(this.mywigets + '/' + template);
  }

  // can be used to add a new widget or update an existing one
  upsertWidget(dashboardId, widget) {
    // create a copy so we don't modify the original
    widget = clone(widget);

    const widgetId = widget.id;

    if (widgetId) {
      // remove the id since that would cause an api failure
      delete widget.id;
    }

    const route = widgetId ?
      this.http.put(this.dashboardRoute + '/' + dashboardId + '/widget/' + widgetId, widget) :
      this.http.post(this.dashboardRoute + '/' + dashboardId + '/widget', widget);

    return route.subscribe((response: any) => {
      return response;
    });
  }

  updateBusItems(id, data) {
    return this.http.put(this.updateBusItemsRoute + '/' + id, data)
      .pipe(catchError(() => null));
  }

  updateDashboardWidgets(id, data) {
    return this.http.put(this.updateDashboardWidgetsRoute + '/' + id, data)
      .pipe(catchError(() => null));
  }

  // can be used to delete existing widget
  deleteWidget(dashboardId, widget) {
    widget = clone(widget);
    const widgetId = widget.id;
    if (widgetId) {
      // remove the id since that would cause an api failure
      delete widget.id;
    }
    const route = this.http.put(this.dashboardRoute + '/' + dashboardId + '/deconsteWidget/' + widgetId, widget);
    return route.subscribe((response: any) => {
      return response;
    },
      (error) => {
        return null;
      });

  }

  // gets count of all dashboards
  count(type) {
    return this.getPromise(this.HygieiaConfig.local ? this.testSearchRoute : this.dashboardCountRoute + '/' + type);
  }

  // gets list of dashboards according to page size (default = 10)
  public searchByPage(params) {
    return this.http.get(this.HygieiaConfig.local ? this.testSearchRoute : this.dashboardRoutePage, { params })
      .pipe(map((response: any) => {
        return { data: response, type: params.type };
      }));
  }

  // gets list of dashboards filtered by title with page size (default = 10)
  filterByTitle(params) {
    return this.http.get(this.HygieiaConfig.local ? this.testSearchRoute : this.dashboardFilterRoutePage, { params })
      .pipe(map((response: any) => {
        return { data: response, type: params.type };
      }));
  }

  // gets count of filtered dashboards for pagination
  filterCount(title, type) {
    return this.http.get(this.HygieiaConfig.local ? this.testSearchRoute : this.dashboardFilterCountRoute + '/' + title + '/' + type)
      .pipe(map((response: any) => response));
  }

  // gets page size
  getPageSize() {
    return this.getPromise(this.HygieiaConfig.local ? this.testSearchRoute : this.dashboardPageSize);
  }

  // gets count of all my dashboards
  myDashboardsCount(type) {
    return this.getPromise(this.HygieiaConfig.local ? this.testSearchRoute : this.myDashboardCountRoute + '/' + type);
  }

  // gets list of my dashboards according to page size (default = 10)
  searchMyDashboardsByPage(params) {
    return this.http.get(this.HygieiaConfig.local ? this.testSearchRoute : this.myDashboardRoutePage, { params })
      .pipe(map((response: any) => {
        return { data: response, type: params.type };
      }));
  }

  // gets list of my dashboards filtered by title with page size (default = 10)
  filterMyDashboardsByTitle(params) {
    return this.http.get(this.HygieiaConfig.local ? this.testSearchRoute : this.myDashboardFilterRoutePage, { params })
      .pipe(map((response: any) => {
        return { data: response, type: params.type };
      }));
  }

  // gets count of filtered dashboards for pagination
  filterMyDashboardCount(title, type) {
    return this.http.get(this.HygieiaConfig.local ? this.testSearchRoute : this.myDashboardFilterCountRoute + '/' + title + '/' + type)
      .pipe(map((response: any) => response));
  }

  // get List of all configurations
  getGeneralConfig(id) {
    return this.getPromise(this.HygieiaConfig.local ? this.dashboardGenconfigRoute + '/fetch' : this.dashboardGenconfigRoute + '/fetch');
  }
  // To save the general config datas
  generalConfigSave(obj) {
    const route = this.dashboardGenconfigRoute;
    return this.http.put(route, obj)
      .subscribe(
        (response: any) => {
          return response;
        },
        (error) => {
          console.log('Error Occured while saving the configuration:' + JSON.stringify(error));
          return error.data;
        });
  }

  updateDashboardScoreSettings(id, scoreEnabled, scoreDisplay) {
    const route = this.updateDashboardScoreSettingsRoute + '/' + id + '?scoreEnabled=' + scoreEnabled + '&scoreDisplay=' + scoreDisplay;
    return this.http.put(route, {})
      .pipe(catchError(() => null));
  }
}

