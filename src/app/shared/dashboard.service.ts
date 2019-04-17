import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import * as _ from 'lodash';
import { Observable, ReplaySubject } from 'rxjs';
import { map, take } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private dashboardRoute = '/api/dashboard/';

  private dashboardSubject = new ReplaySubject<any>(1);

  public dashboardConfig$ = this.dashboardSubject.asObservable();

  constructor(private http: HttpClient) { }

  // Retrieve a new dashboard from the API, and push it to subscribers
  loadDashboard(dashboardId: string) {
    this.http.get(this.dashboardRoute + dashboardId).subscribe(res => this.dashboardSubject.next(res));
  }

  // Clone the passed widget config, and post the updated widget to the API
  upsertWidget(dashboardId: string, widgetConfig: any): Observable<any> {
    widgetConfig = _.cloneDeep(widgetConfig);

    const widgetId = widgetConfig.id;
    if (widgetId) {
      delete widgetConfig.id;
    }

    const apiCall = widgetId ?
      this.http.put(this.dashboardRoute + dashboardId + '/widget/' + widgetId, widgetConfig) :
      this.http.post(this.dashboardRoute + dashboardId + '/widget', widgetConfig);

    return apiCall;
  }

  // Take a new component and config returned by the API, and update the data locally.
  // Push this new version to subscibers.
  upsertLocally(newComponent: any, newConfig: any) {
    // Find and update component
    let tempDashboard$ = this.dashboardConfig$.pipe(take(1), map(dashboard => {
      if (newComponent == null) {
        return dashboard;
      }
      let foundComponent = false;
      dashboard.application.components.forEach((component: any, index: number) => {
        if (component.id === newComponent.id) {
          foundComponent = true;
          dashboard.application.components[index] = newComponent;
        }
      });
      if (!foundComponent) {
        dashboard.application.components.push(newComponent);
      }
      return dashboard;
    }));

    // Find and update config
    tempDashboard$ = tempDashboard$.pipe(map(dashboard => {
      let foundMatch = false;
      const filteredWidgets = dashboard.widgets.filter((config: any) => config.options.id === newConfig.options.id);
      filteredWidgets.forEach((config: any, index: number) => {
        foundMatch = true;
        dashboard.widgets[index] = _.extend(config, newConfig);
      });
      if (!foundMatch) {
        dashboard.widgets.push(newConfig);
      }
      return dashboard;
    }));

    tempDashboard$.subscribe(dashboard => this.dashboardSubject.next(dashboard));
  }

}
