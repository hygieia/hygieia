import { Injectable } from '@angular/core';
import {IDashboardResponse} from './dashboard-view';
import {Observable} from 'rxjs/internal/Observable';
import {HttpClient, HttpParams} from '@angular/common/http';
import {map} from 'rxjs/operators';

@Injectable()
export class DashboardViewService {

  constructor(private http: HttpClient) {}


  getTeamDashboard(params: HttpParams): Observable<IDashboardResponse> {
    const path = '/api/dashboard/5963b4c4f35ff608caafc865';
    return this.get(null, path);
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
