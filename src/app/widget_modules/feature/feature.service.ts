import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map} from 'rxjs/operators';
import { IFeatureResponse } from './interfaces';

@Injectable({
  providedIn: 'root'
})
export class FeatureService {

  featureAggregateSprintEstimates = '/api/feature/estimates/aggregatedsprints/';
  featureWip = '/api/feature/estimates/super/';
  featureIterations = '/api/iteration';

  constructor(private http: HttpClient) { }

  fetchAggregateSprintEstimates(component, teamId, projectId, agileType) {
    const params = {
      params: new HttpParams().set('component', component).set('teamId', teamId).set('projectId', projectId).set('agileType', agileType)
    };
    return this.http.get<IFeatureResponse>(this.featureAggregateSprintEstimates, params).pipe(
      map(response => response.result));
  }

  fetchFeatureWip(component, teamId, projectId, agileType) {
    const params = {
      params: new HttpParams().set('component', component).set('teamId', teamId).set('projectId', projectId).set('agileType', agileType)
    };
    return this.http.get<IFeatureResponse>(this.featureWip, params).pipe(
      map(response => response.result));
  }

  fetchIterations(component, teamId, projectId, agileType) {
    const params = {
      params: new HttpParams().set('component', component).set('teamId', teamId).set('projectId', projectId).set('agileType', agileType)
    };
    return this.http.get<IFeatureResponse>(this.featureIterations, params).pipe(
      map(response => response.result));
  }
}
