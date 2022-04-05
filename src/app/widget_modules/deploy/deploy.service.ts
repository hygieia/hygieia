import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IDeploy, IDeployResponse } from './interfaces';

@Injectable({
  providedIn: 'root'
})
export class DeployService {
  deployDetailRoute = '/api/deploy/status/';

  constructor(private http: HttpClient) { }

  fetchDetails(componentId: string): Observable<IDeploy[]> {
    return this.http.get<IDeployResponse>(this.deployDetailRoute + componentId).pipe(map(result => result.result));
  }
}

