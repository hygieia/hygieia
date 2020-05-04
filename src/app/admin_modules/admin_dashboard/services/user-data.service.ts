import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class UserDataService {

  adminRoute = '/api/admin';
  userRoute = '/api/users';

  constructor(private http: HttpClient) { }

  apitokens() {
    const route = this.adminRoute + '/apitokens';
    return this.http.get(route);
  }

  users() {
    return this.http.get(this.userRoute);
  }


  createToken(apitoken) {
    const route = this.adminRoute + '/createToken';
    return this.http.post(route, apitoken);
  }

  getFeatureFlagsData() {
    const featureFlagRoute = this.adminRoute + '/featureFlags';
    return this.http.get(featureFlagRoute);
  }

  createOrUpdateFeatureFlags(flags) {
    const featureFlagAddUpdateRoute = this.adminRoute + '/addOrUpdateFeatureFlags';
    const json = {
      json: (flags)
    };
    return this.http.post(featureFlagAddUpdateRoute, json);
  }

  deleteFeatureFlags(id) {
    const featureFlagsDeleteRoute = this.adminRoute + '/deleteFeatureFlags/';
    return this.http.delete(featureFlagsDeleteRoute + id);
  }

  deleteToken(id) {
    const route = this.adminRoute + '/deleteToken';
    return this.http.delete(route + '/' + id);
  }

  updateToken(apiToken, id) {
    const route = this.adminRoute + '/updateToken';
    return this.http.post(route + '/' + id, apiToken);
  }

   promoteUserToAdmin(user) {
    const route = this.adminRoute + '/users/addAdmin';
    return this.http.post(route, user);
  }

   demoteUserFromAdmin(user) {
    const route = this.adminRoute + '/users/removeAdmin';
    return this.http.post(route, user);
  }
}
