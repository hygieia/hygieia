import { HttpClient, HttpParams } from '@angular/common/http';
import { ICollItem } from 'src/app/viewer_modules/collector-item/interfaces';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { IOpensourceScan, IOpensourceScanResponse } from './interfaces';

@Injectable({
  providedIn: 'root'
})
export class OpensourceScanService {

  collectorItemsEndpoint = '/api/collector/item/component';
  detailRoute = '/api/ui-widget/library-policy';

  constructor(private http: HttpClient) { }

  getLibraryPolicyCollectorItems(componentId: string): Observable<ICollItem[]> {
    const params = {
      params: new HttpParams().set('type', 'LibraryPolicy')
    };
    return this.http.get<ICollItem[]>(`${this.collectorItemsEndpoint}/${componentId}`, params);
  }

  fetchDetails(componentId: string, collectorItemId: string): Observable<IOpensourceScan[]> {
    const params = {
      params: new HttpParams().set('componentId', componentId).set('collectorItemId', collectorItemId)
    };
    return this.http.get<IOpensourceScanResponse>(this.detailRoute, params).pipe(
      map(response => response.result));
  }

  refreshProject(refreshLink: string) {
    return this.http.get(refreshLink);
  }

}
