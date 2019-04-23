import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs/internal/Observable";
import {IDashboardResponse} from "./dashboard-view/dashboard-view";
import {IDashboardsResponse} from "../../landing_page/dashboard-list/dashboard-list";
import {map} from "rxjs/operators";
import {HttpClientModule} from '@angular/common/http';

@Injectable()
export class TeamDashboardService {

  constructor(private http: HttpClient) {}


  getTeamDashboard(params: HttpParams): Observable<IDashboardResponse>{
    const path = 'api/dashboard';
    return this.get(params,path);
  }

  get(params: HttpParams, path: string): Observable<IDashboardResponse> {
    return this.http.get<any>(path, { params, observe: 'response' }).pipe(
      map(data => {
        return {
          data: data.body,
          } as IDashboardResponse;
      }));
  }

}
