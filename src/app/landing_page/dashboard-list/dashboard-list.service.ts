import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { Observable, of } from 'rxjs';

import { IDashboardsResponse } from './dashboard-list';
import { AuthService } from '../../core/services/auth.service';

@Injectable()
export class DashboardListService {

  constructor(private http: HttpClient, private authService: AuthService) { }

  getMyDashboards(params: HttpParams): Observable<IDashboardsResponse> {
    // Checking Auth so that we don't make unnecessary api calls
    if (this.authService.isAuthenticated()) {
      return this.http.get<any>(' /api/dashboard/mydashboard/page/filter', { params, observe: 'response' }).pipe(
        map(data => {
          return {
            data: data.body,
            total: data.headers.get('totalentities')
          } as IDashboardsResponse;
        }));
    } else {
      return of( {} as IDashboardsResponse);
    }
  }
  getAllDashboards(params: HttpParams): Observable<IDashboardsResponse> {
    return this.http.get<any>(' /api/dashboard/page/filter', { params, observe: 'response' }).pipe(
      map(data => {
        return {
          data: data.body,
          total: data.headers.get('totalentities')
        } as IDashboardsResponse;
      }));

  }
}
