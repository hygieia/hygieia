import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ITest, TestType } from './interfaces';
import { map } from 'rxjs/operators';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TestService {

  testDetailRoute = '/api/quality/test';

  constructor(private http: HttpClient) { }

  fetchTestResults(componentId: string, max: number, depth: number, type: TestType[]): Observable<ITest[]> {
    const params = {
      params: new HttpParams().set('componentId', componentId)
                              .set('depth', depth.toFixed(0))
                              .set('types', type.toString())
                              .set('max', max.toFixed(0))
    };
    return this.http.get<ITest>(this.testDetailRoute, params).pipe(map(response => response.result));
  }
}
