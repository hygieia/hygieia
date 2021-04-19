import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CollectorItem } from '../dashboard/admin-dashboard/model/collectors-item';

@Injectable({
    providedIn: 'root'
})
export class CollectorsService {
    getAllCollectorsRoute = '/api/collector/';

    constructor(private http: HttpClient) {}

    getAllCollectors() {
        return this.http.get<CollectorItem[]>(this.getAllCollectorsRoute);
    }
}
