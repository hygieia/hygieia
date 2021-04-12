import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ISecurityScan} from './security-scan-interfaces';

@Injectable({
  providedIn: 'root'
})
export class SecurityScanService {

  securityScanDetailEndPoint = '/api/quality/security-analysis/all';

  constructor(private http: HttpClient) {}

  getSecurityScanDetails(componentId: string): Observable<ISecurityScan[]> {
    return this.http.get<ISecurityScan[]>(this.securityScanDetailEndPoint, this.getHttpParams(componentId));
  }

  private getHttpParams(componentId: string) {
    return { params : new HttpParams().set('componentId', componentId)};
  }

}
