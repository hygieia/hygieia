import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { IRepo, IRepoResponse} from './interfaces';
import { ICollItem } from 'src/app/viewer_modules/collector-item/interfaces';


@Injectable({
  providedIn: 'root'
})
export class RepoService {

  repoIssueDetailRoute = '/api/ui-widget/gitrequests/type/issue';
  repoPullDetailRoute = '/api/ui-widget/gitrequests/type/pull';
  repoCommitDetailRoute = '/api/ui-widget/commit/';
  collectorItemsEndpoint = '/api/collector/item/component';


  constructor(private http: HttpClient) { }

  fetchSCMCollectorItems(componentId: string): Observable<ICollItem[]> {
    return this.http.get<ICollItem[]>(`${this.collectorItemsEndpoint}/${componentId}`, this.getCollectorItemParams());
  }

  private getCollectorItemParams() {
    return { params : new HttpParams().set('type', 'SCM')};
  }

  private getCommitParams(componentId: string, collectorItemId: string, numberOfDays: number) {
    return {
      params: new HttpParams().set('componentId', componentId).set('collectorItemId', collectorItemId).set('numberOfDays', numberOfDays.toFixed(0))
    };
  }

  fetchIssues(componentId: string, collectorItemId: string): Observable<IRepo[]> {
    return this.http.get<IRepoResponse>(this.repoIssueDetailRoute, this.getParams(componentId, collectorItemId)).pipe(
      map(response => response.result));
  }

  fetchCommits(componentId: string, collectorItemId: string, numberOfDays: number): Observable<IRepo[]> {
    return this.http.get<IRepoResponse>(this.repoCommitDetailRoute, this.getCommitParams(componentId, collectorItemId, numberOfDays)).pipe(
      map(response => response.result));
  }

  private getParams(componentId: string, collectorItemId: string) {
    return {
      params: new HttpParams().set('componentId', componentId).set('collectorItemId', collectorItemId)
    };
  }

  fetchPullRequests(componentId: string, collectorItemId: string): Observable<IRepo[]> {
    return this.http.get<IRepoResponse>(this.repoPullDetailRoute, this.getParams(componentId, collectorItemId)).pipe(
      map(response => response.result));
  }
}

