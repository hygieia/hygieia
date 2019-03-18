import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map, share } from 'rxjs/operators';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BuildResponse, Build } from './interfaces';

@Injectable({
    providedIn: 'root'
})
export class BuildService {

    buildDetailRoute = '/api/build/';

    constructor(private http: HttpClient) { }

    fetchDetails(): Observable<Build[]> {
        const params = {
            params: new HttpParams().set('componentId', '59f88f5e6a3cf205f312c62e').set('numberOfDays', '14')
        };
        return this.http.get<BuildResponse>(this.buildDetailRoute, params).pipe(map(response => response.result));
    }
}
