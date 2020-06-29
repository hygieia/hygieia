import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {of, Observable, zip} from 'rxjs';
import {map} from 'rxjs/operators';
import {IAuditResult, IDashboardCI, IDashboardCIResponse} from './interfaces';
import {IBuildResponse} from '../../widget_modules/build/interfaces';
import {IOpensourceScanResponse} from '../../widget_modules/opensource-scan/interfaces';
import {IDeployResponse} from '../../widget_modules/deploy/interfaces';
import {IStaticAnalysisResponse} from '../../widget_modules/static-analysis/interfaces';
import {ISecurityScanResponse} from '../../widget_modules/security-scan/security-scan-interfaces';
import {ITest} from '../../widget_modules/test/interfaces';
import {RepoService} from '../../widget_modules/repo/repo.service';

@Injectable({
  providedIn: 'root'
})
export class CollectorItemService {

  private dashboardByTitleRoute = '/api/dashboard/page/filter?search=';
  private dashboardByCIBusAppNameRoute = '/api/dashboard/configItemComponent/';
  private auditRoute = '/apiaudit/auditresult/dashboard/title/';
  private buildRoute = '/api/build/';
  private libraryPolicyRoute = '/api/libraryPolicy/';
  private staticAnalysisRoute = '/api/quality/static-analysis';
  private deployDetailRoute = '/api/deploy/status/';
  private securityScanDetailRoute = '/api/quality/security-analysis';
  private testDetailRoute = '/api/quality/test';

  private readonly MAX_RECORDS = 1000;
  private readonly NUM_OF_DAYS = 60;

  constructor(private http: HttpClient, private repoService: RepoService) { }

  getDashboardByCI(ci: string): Observable<IDashboardCI[]> {
    if ( !ci ) { return of([]); }
    return this.http.get<IDashboardCIResponse>(this.dashboardByCIBusAppNameRoute + ci).pipe(
      map(response => response.result));
  }

  getDashboardByTitle(title: string): Observable<IDashboardCI[]> {
    if ( !title ) { return of([]); }
    return this.http.get<IDashboardCI[]>(this.dashboardByTitleRoute + title);
  }

  private getParams(componentId: string, max: number) {
    return {
      params: new HttpParams().set('componentId', componentId).set('max', max.toString())
    };
  }

  getCollectorItemDetails(dashboardTitle: string, componentId: string, collector: string): Observable<any[]> {
    if ( !collector ) { return of ([]); }

    if ( collector.match('Audit') ) {
      return this.getAuditResult(dashboardTitle);
    } else if ( collector.match('Artifact') ) {
      return this.getArtifactResult(componentId);
    } else if ( collector.match('Build') ) {
      return this.getBuildResult(componentId);
    } else if ( collector.match('CodeQuality') ) {
      return this.getCodeQualityResult(componentId);
    } else if ( collector.match('Deployment') ) {
      return this.getDeploymentResult(componentId);
    } else if ( collector.match('Incident') ) {
      return this.getIncidentResult(componentId);
    } else if ( collector.match('LibraryPolicy') ) {
      return this.getLibraryPolicyResult(componentId);
    } else if ( collector.match('SCM') ) {
      return this.getSCMResult(componentId);
    } else if ( collector.match('StaticSecurityScan') ) {
      return this.getStaticSecurityScanResult(componentId);
    } else if ( collector.match('Test') ) {
      return this.getTestResult(componentId);
    } else {
      return of ([]);
    }
  }

  private getAuditResult(dashboardTitle: string) {
    if ( !dashboardTitle ) { return of([]); }
    return this.http.get<IAuditResult[]>(this.auditRoute + dashboardTitle);
  }

  private getBuildResult(componentId: string) {
    if ( !componentId ) { return of([]); }
    return this.http.get<IBuildResponse>(this.buildRoute, this.getParams(componentId, this.MAX_RECORDS)).pipe(
      map(response => response.result));
  }

  private getLibraryPolicyResult(componentId: string) {
    if ( !componentId ) { return of([]); }
    return this.http.get<IOpensourceScanResponse>(this.libraryPolicyRoute, this.getParams(componentId, this.MAX_RECORDS)).pipe(
      map(response => response.result));
  }

  private getCodeQualityResult(componentId: string) {
    if ( !componentId ) { return of([]); }
    return this.http.get<IStaticAnalysisResponse>(this.staticAnalysisRoute, this.getParams(componentId, this.MAX_RECORDS)).pipe(
      map(response => response.result));
  }

  private getStaticSecurityScanResult(componentId: string) {
    if ( !componentId ) { return of([]); }
    return this.http.get<ISecurityScanResponse>(this.securityScanDetailRoute, this.getParams(componentId, this.MAX_RECORDS))
      .pipe(map(response => response.result));
  }

  private getTestResult(componentId: string) {
    return this.http.get<ITest>(this.testDetailRoute, this.getParams(componentId, this.MAX_RECORDS))
      .pipe(map(response => response.result));
  }

  private getDeploymentResult(componentId: string) {
    if ( !componentId ) { return of([]); }
    return this.http.get<IDeployResponse>(this.deployDetailRoute + componentId).pipe(
      map(response => response.result));
  }

  private getIncidentResult(componentId: string) {
    return of([]);
  }

  private getArtifactResult(componentId: string) {
    return of([]);
  }

  private getSCMResult(componentId: string) {
    if ( !componentId ) { return of([]); }
    const commits = this.repoService.fetchCommits(componentId, this.NUM_OF_DAYS);
    const pullRequests = this.repoService.fetchPullRequests(componentId, this.NUM_OF_DAYS);
    const issues = this.repoService.fetchIssues(componentId, this.NUM_OF_DAYS);
    return zip(commits, pullRequests, issues).pipe(map(res => [].concat(...res)));
  }
}
