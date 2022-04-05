import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CollectorService {

  // URL for items by type
  private itemsByTypeRoute = '/api/collector/item/type/';

  private itemsByTypeRouteBySearchField = '/api/collector/item/type/searchField/';
  // URL for items by id
  private itemRoute = '/api/collector/item/';

  private collectorsByTypeRoute = '/api/collector/type/';

  constructor(private http: HttpClient) { }

  getItemsByType(type: string, params: any): Observable<any> {
    return this.http.get(this.itemsByTypeRoute + type, {params});
  }

  getItemsByTypeBySearchField(type: string, params: any): Observable<any> {
    return this.http.get(this.itemsByTypeRouteBySearchField + type, {params});
  }

  searchItems(type: string, filter: string): Observable<any> {
    return this.getItemsByType(type, {search: filter, size: 20});
  }

  searchItemsBySearchField(type: string, filter: string, searchFieldInput: string): Observable<any> {
    return this.getItemsByTypeBySearchField(type, {search: filter, searchField: searchFieldInput, size: 20 });
  }

  getItemsById(id: string): Observable<any> {
    return this.http.get(this.itemRoute + id);
  }

  collectorsByType(collectorType): Observable<any> {
    return this.http.get(this.collectorsByTypeRoute + collectorType);
  }
}
