import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {InfraScan, InfraScanResponse} from './infra-scan-interfaces';
import {map} from 'rxjs/operators';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class InfraScanService {

  constructor(private http: HttpClient) { }

  getInfraScanDetails(params: any): Observable<InfraScan[]>  {
    return this.http.get<InfraScanResponse>('/api/ui-widget/infra-scan', { params })
      .pipe(map(response => response.result));
  }
}
