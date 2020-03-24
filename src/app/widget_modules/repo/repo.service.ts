import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { IRepo, IRepoResponse} from './interfaces';

@Injectable({
  providedIn: 'root'
})
export class RepoService {

  repoIssueDetailRoute = '/api/gitrequests/type/issue/state/all/';
  repoPullDetailRoute = '/api/gitrequests/type/pull/state/all/';
  repoCommitDetailRoute = '/api/commit/';

  constructor(private http: HttpClient) { }

  private getParams(componentId: string, numberOfDays: number) {
    return {
      params: new HttpParams().set('componentId', componentId).set('numberOfDays', numberOfDays.toFixed(0))
    };
  }

  fetchIssues(componentId: string, numberOfDays: number): Observable<IRepo[]> {
    return this.http.get<IRepoResponse>(this.repoIssueDetailRoute, this.getParams(componentId, numberOfDays)).pipe(
      map(response => response.result));
  }

  fetchCommits(componentId: string, numberOfDays: number): Observable<IRepo[]> {
    return this.http.get<IRepoResponse>(this.repoCommitDetailRoute, this.getParams(componentId, numberOfDays)).pipe(
      map(response => response.result));
  }

  fetchPullRequests(componentId: string, numberOfDays: number): Observable<IRepo[]> {
    return this.http.get<IRepoResponse>(this.repoPullDetailRoute, this.getParams(componentId, numberOfDays)).pipe(
      map(response => response.result));
  }
}

