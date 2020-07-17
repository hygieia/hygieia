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
  getDashboardTitleOrig(data) {
    let subName;

    if (data.name === undefined) {
      subName = data.title;
    } else {
      subName = data.name.substring(0, data.name.indexOf('-'));
    }
    return subName ? subName : data.name;
  }
  getBusSerToolTipText() {
    return 'A top level name which support Business function.';
  }

  getBusAppToolTipText() {
    return 'A Business Application (BAP) CI is a CI Subtype in the application which supports business function (Top level).';
  }
}
