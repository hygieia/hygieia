import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})

export class AdminDashboardService {
  businessApplicationId;
  businessServiceId;
  getDashboardType() {
    return {
      PRODUCT: 'product',
      TEAM: 'team'
    };
  }

  constructor() {
  }

  getBusServValueBasedOnType(dashboardType, value) {
    return dashboardType === this.getDashboardType().PRODUCT ? '' : value;
  }

  getDashboardTitle(data) {
    let title = data.title;
    const businessServiceName = data.configurationItemBusServName ? '-' + data.configurationItemBusServName : '';
    const businessApplicationName = data.configurationItemBusAppName ? '-' + data.configurationItemBusAppName : '';
    const applicationName = data.application && data.application.name ? '-' + data.application.name : '';

    if (businessServiceName !== '' || businessApplicationName !== '') {
      title = title + businessServiceName + businessApplicationName;
    } else {
      title = title + applicationName;
    }

    return title;
  }

  setBusinessServiceId(id) {
    this.businessServiceId = id;
  }
  setBusinessApplicationId(id) {
    this.businessApplicationId = id;
  }
  getBusinessServiceId(name) {
    let value = null;
    if (name) {
      value = this.businessServiceId;
    }
    return value;
  }
  getBusinessApplicationId(name) {
    let value = null;
    if (name) {
      value = this.businessApplicationId;
    }
    return value;
  }
}
