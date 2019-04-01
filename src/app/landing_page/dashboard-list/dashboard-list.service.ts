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
      const path = '/api/dashboard/mydashboard/page/filter';
      return this.get(params, path);
    } else {
      return of( {} as IDashboardsResponse);
    }
  }
  getAllDashboards(params: HttpParams): Observable<IDashboardsResponse> {
    const path = ' /api/dashboard/page/filter';
    return this.get(params, path);

  }

  get(params: HttpParams, path: string): Observable<IDashboardsResponse> {
    return this.http.get<any>(path, { params, observe: 'response' }).pipe(
      map(data => {
        return {
          data: data.body,
          total: data.headers.get('totalentities')
        } as IDashboardsResponse;
      }));
  }
}
