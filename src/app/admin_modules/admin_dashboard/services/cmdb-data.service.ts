import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class CmdbDataService {

   testConfigItemRoute = '';
   dashboardConfigItemListRoute = '/api/cmdb/configItem';
   HygieiaConfig: any = { local: null };

  constructor(private http: HttpClient) { }

     getConfigItemList(type, params) {
        return this.http
        .get(this.HygieiaConfig.local ? this.testConfigItemRoute : this.dashboardConfigItemListRoute + '/' + type, {params});
    }

    getConfigItems(type, params) {
    return this.http.get(this.dashboardConfigItemListRoute + '/' + type, {params});
  }
}
