import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/services/auth.service';
import { DashboardDataService } from './dashboard-data.service';
import { AdminDashboardService } from './dashboard.service';
import { map } from 'rxjs/internal/operators/map';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class PaginationWrapperService {

  currentPage = 0;
  pageSize = 10;
  currentPageMyDash = 0;
  searchFilter = '';
  dashboards: any;
  dashboardTypes: any;
  totalItems: any;
  totalItemsMyDash: any;
  mydash: any;
  username: any;
  DashboardType: any = {
    PRODUCT: 'product',
    TEAM: 'team'
  };

  constructor(private authService: AuthService, private dashboardData: DashboardDataService,
              private dashboardService: AdminDashboardService) {
    this.username = this.authService.getUserName();
  }

  calculateTotalItems(type) {
    return this.dashboardData.count(type)
      .pipe(map((data: any) => {
        this.totalItems = data;
        return data;
      }));
  }

  calculateTotalItemsMyDash(type) {
    return this.dashboardData.myDashboardsCount(type).subscribe((data) => {
      this.totalItemsMyDash = data;
    });
  }

  getTotalItems() {
    return this.totalItems;
  }

  getTotalItemsMyDash() {
    return this.totalItemsMyDash;
  }

  getCurrentPage() {
    return this.currentPage;
  }

  getPageSize() {
    return this.pageSize;
  }

  getDashboards() {
    return this.dashboards;
  }

  getMyDashboards() {
    return this.mydash;
  }

  setDashboards(paramDashboards) {
    this.dashboards = paramDashboards;
  }

  getInvalidAppOrCompError(data) {
    let showError = false;
    if ((data.configurationItemBusServName !== undefined && !data.validServiceName)
      || (data.configurationItemBusAppName !== undefined && !data.validAppName)) {
      showError = true;
    }

    return showError;
  }

  pageChangeHandler(pageNumber, type) {
    this.currentPage = pageNumber;

    if (this.searchFilter === '') {
      return this.dashboardData.searchByPage({ search: '', size: this.pageSize, page: pageNumber - 1, type })
        .pipe(catchError(this.processDashboardError))
        .pipe(map(this.processDashboardResponse));
    } else {
      return this.dashboardData.filterByTitle({ search: this.searchFilter, size: this.pageSize, page: pageNumber - 1, type })
        .pipe(catchError(this.processDashboardError))
        .pipe(map(this.processDashboardFilterResponse));
    }
  }

  pageChangeHandlerForMyDash = (pageNumber, type) => {
    this.currentPageMyDash = pageNumber;

    if (this.searchFilter === '') {
      return this.dashboardData.searchMyDashboardsByPage({ size: this.pageSize, page: pageNumber - 1, username: this.username, type })
        .pipe(catchError(this.processMyDashboardError))
        .pipe(map(this.processMyDashboardResponse));
    } else {
      return this.dashboardData.filterMyDashboardsByTitle({ search: this.searchFilter, size: this.pageSize, page: pageNumber - 1 })
        .pipe(catchError(this.processFilterMyDashboardResponse))
        .pipe(map(this.processMyDashboardResponse));
    }
  }

  public processDashboardResponse = (response) => {
    const data = response.data;
    const type = response.type;

    // add dashboards to list
    this.dashboards = [];
    const dashboardsLocal = [];

    data.forEach((v, x) => {
      const board = {
        id: data[x].id,
        name: this.dashboardService.getDashboardTitle(data[x]),
        type: data[x].type,
        validServiceName: data[x].validServiceName,
        validAppName: data[x].validAppName,
        configurationItemBusServName: data[x].configurationItemBusServName,
        configurationItemBusAppName: data[x].configurationItemBusAppName,
        isProduct: data[x].type && data[x].type.toLowerCase() === this.DashboardType.PRODUCT.toLowerCase(),
        scoreEnabled: data[x].scoreEnabled,
        scoreDisplay: data[x].scoreDisplay
      };

      if (board.isProduct) {
      }
      dashboardsLocal.push(board);
    });

    this.dashboards = dashboardsLocal;
    this.dashboardData.count(type).subscribe((result: any) => {
      this.totalItems = result;
    });

    return dashboardsLocal;
  }

  public processDashboardFilterResponse = (response) => {
    const data = response.data;
    const type = response.type;

    this.dashboards = [];
    const dashboardsLocal = [];

    data.forEach((v, x) => {
      const board = {
        id: data[x].id,
        name: this.dashboardService.getDashboardTitle(data[x]),
        isProduct: data[x].type && data[x].type.toLowerCase() === this.DashboardType.PRODUCT.toLowerCase()
      };

      if (board.isProduct) {
      }
      dashboardsLocal.push(board);
    });

    this.dashboards = dashboardsLocal;
    if (this.searchFilter === '') {
      this.dashboardData.count(type).subscribe((result: any) => {
        this.totalItems = result;
      });
    }

    return dashboardsLocal;
  }

  public processDashboardError = (data) => {
    this.dashboards = [];
    return this.dashboards;
  }

  public processMyDashboardResponse = (response) => {
    const mydata = response.data;
    const type = response.type;

    // add dashboards to list
    this.mydash = [];
    const dashboardsLocal = [];

    mydata.forEach((v, x) => {
      const showErrorVal = this.getInvalidAppOrCompError(mydata[x]);
      dashboardsLocal.push({
        id: mydata[x].id,
        name: this.dashboardService.getDashboardTitle(mydata[x]),
        type: mydata[x].type,
        isProduct: mydata[x].type && mydata[x].type.toLowerCase() === this.DashboardType.PRODUCT.toLowerCase(),
        validServiceName: mydata[x].validServiceName,
        validAppName: mydata[x].validAppName,
        configurationItemBusServName: mydata[x].configurationItemBusServName,
        configurationItemBusAppName: mydata[x].configurationItemBusAppName,
        showError: showErrorVal,
        scoreEnabled: mydata[x].scoreEnabled,
        scoreDisplay: mydata[x].scoreDisplay
      });
    });

    this.mydash = dashboardsLocal;
    this.dashboardData.myDashboardsCount(type).subscribe((data) => {
      this.totalItemsMyDash = data;
    });


    return dashboardsLocal;
  }

  public processFilterMyDashboardResponse = (response) => {
    const mydata = response.data;
    const type = response.type;

    // add dashboards to list
    this.mydash = [];
    const dashboardsLocal = [];

    mydata.forEach((v, x) => {
      const showErrorVal = this.getInvalidAppOrCompError(mydata[x]);
      dashboardsLocal.push({
        id: mydata[x].id,
        name: this.dashboardService.getDashboardTitle(mydata[x]),
        type: mydata[x].type,
        isProduct: mydata[x].type && mydata[x].type.toLowerCase() === this.DashboardType.PRODUCT.toLowerCase(),
        validServiceName: mydata[x].validServiceName,
        validAppName: mydata[x].validAppName,
        configurationItemBusServName: mydata[x].configurationItemBusServName,
        configurationItemBusAppName: mydata[x].configurationItemBusAppName,
        showError: showErrorVal,
        scoreEnabled: mydata[x].scoreEnabled,
        scoreDisplay: mydata[x].scoreDisplay
      });
    });

    this.mydash = dashboardsLocal;
    if (this.searchFilter === '') {
      this.dashboardData.myDashboardsCount(type).subscribe((result: any) => {
        this.totalItemsMyDash = result;
      });
    }

    return dashboardsLocal;
  }

  public processMyDashboardError = (data) => {
    this.mydash = [];
    return this.mydash;
  }

  filterByTitle = (title, type) => {
    this.currentPage = 0;
    this.currentPageMyDash = 0;
    this.searchFilter = title;
    const promises = [];

    if (title === '') {
      promises.push(this.dashboardData.searchByPage({ search: '', size: this.pageSize, page: 0, type })
        .pipe(catchError(this.processDashboardError))
        .pipe(map(this.processDashboardResponse)));

      promises.push(this.dashboardData.searchMyDashboardsByPage({ username: this.username, type, size: this.pageSize, page: 0 })
        .pipe(catchError(this.processMyDashboardError))
        .pipe(map(this.processMyDashboardResponse)));
    } else {
      promises.push(this.dashboardData.filterCount(title, type)
      .pipe(map((data: any) => {
        this.totalItems = data;
        return data;
     })));

      promises.push(this.dashboardData.filterByTitle({ search: title, type, size: this.pageSize, page: 0 })
        .pipe(catchError(this.processDashboardError))
        .pipe(map(this.processDashboardFilterResponse)));

      promises.push(this.dashboardData.filterMyDashboardCount(title, type)
      .pipe(map((data: any) => {
        this.totalItemsMyDash = data;
        return data;
     })));

      promises.push(this.dashboardData.filterMyDashboardsByTitle({ search: title, type, size: this.pageSize, page: 0 })
        .pipe(catchError(this.processMyDashboardError))
        .pipe(map(this.processFilterMyDashboardResponse)));
    }

    return promises;
  }
}



