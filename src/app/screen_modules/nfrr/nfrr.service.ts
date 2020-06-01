import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, map, mergeMap} from 'rxjs/operators';
import {IAudit, IAuditResponsePage} from '../../shared/interfaces';
import {Observable, of} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NfrrService {

  private httpOptions = { headers: new HttpHeaders({ 'Content-Type': 'application/json' })};
  private auditAllRoute = '/apiaudit/auditresult/dashboard/all/pages';
  private auditByLobRoute = '/apiaudit/auditresult/lob';

  constructor(private http: HttpClient) {
  }

  getAuditMetricsAll(): Observable<IAudit[]> {
    return this.getTotalElementsCount(this.auditAllRoute)
      .pipe(mergeMap(total => this.getAuditMetrics(`${this.auditAllRoute}?size=${total}`)));
  }

  getAuditMetricsByLob(lob: string): Observable<IAudit[]> {
    const url = `${this.auditByLobRoute}/${lob}/pages`;
    return this.getTotalElementsCount(url)
      .pipe(mergeMap(total => this.getAuditMetrics(`${url}?size=${total}`)));
  }

  getTotalElementsCount(url: string): Observable<number> {
    return this.http.get<IAuditResponsePage>(url).pipe(map(response => response.totalElements),
      catchError(err => of(0)));
  }

  private getAuditMetrics(url: string): Observable<IAudit[]> {
    return this.http.get<IAuditResponsePage>(url, this.httpOptions).pipe(map(response => {
      return response.content.map((auditResult) => {
        return {
          lineOfBusiness: auditResult.lineOfBusiness,
          auditType: auditResult.auditType,
          auditStatus: auditResult.auditStatus,
          auditTypeStatus: auditResult.auditTypeStatus,
          timestamp: auditResult.timestamp } as IAudit;
      });
    }), catchError(err => of([] as IAudit[])));
  }
}
