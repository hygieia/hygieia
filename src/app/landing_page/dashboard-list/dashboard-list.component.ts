import { Component, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { HttpParams } from '@angular/common/http';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';

import { IDashboards } from './dashboard-list';
import { DashboardListService } from './dashboard-list.service';
import { IPaginationParams } from '../../shared/interfaces';
@Component({
  selector: 'app-dashboard-list',
  templateUrl: './dashboard-list.component.html',
  styleUrls: ['./dashboard-list.component.scss']
})
export class DashboardListComponent implements OnInit {
  dashboardType = '';
  queryField: FormControl = new FormControl();
  myDashboards: IDashboards[] = [];
  allDashboards: IDashboards[] = [];
  dashboardCollectionSize: string;
  myDashboardCollectionSize: string;
  defaultPageSize = '10';

  constructor(private landingPageService: DashboardListService) { }

  ngOnInit() {
    this.findMyDashboards(this.paramBuilder(0, this.defaultPageSize));
    this.findAllDashboards(this.paramBuilder(0, this.defaultPageSize));
    // Query for pull filtered owner dashboards
    this.queryField.valueChanges.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      switchMap(() => {
        return this.landingPageService.getMyDashboards(this.paramBuilder(0, this.defaultPageSize) ); })
    ).subscribe(response => {
      this.myDashboards = response.data;
      this.myDashboardCollectionSize = response.total;
    });
    // Query for pull filtered 'All' dashboards
    this.queryField.valueChanges.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      switchMap(() => {
        return this.landingPageService.getAllDashboards( this.paramBuilder(0, this.defaultPageSize) ); })
    )
      .subscribe(response => {
        this.allDashboards = response.data;
        this.dashboardCollectionSize = response.total;
      });
  }

  // Default function call for pulling users dashboards
  findMyDashboards(params: HttpParams): void {
    this.landingPageService.getMyDashboards(params).subscribe(
      response => {
        this.myDashboards = response.data;
        this.myDashboardCollectionSize = response.total;
      },
      error => console.log(error)
    );
  }

  // Default function call for pulling all dashboards
  findAllDashboards(params: HttpParams): void {
    this.landingPageService.getAllDashboards(params).subscribe(
      response => {
        this.allDashboards = response.data;
        this.dashboardCollectionSize = response.total;
      },
      error => console.log(error)
    );
  }

  // Pagination page change function call
  myDashboardPageChange(params: IPaginationParams) {
    this.findMyDashboards(this.paramBuilder(params.page - 1, params.pageSize));
  }

  // Pagination page change function call
  allDashboardPageChange(params: IPaginationParams) {
    this.findAllDashboards( this.paramBuilder(params.page - 1, params.pageSize));
  }

  setDashboardType(type: string) {
    this.dashboardType = type;
    this.findMyDashboards(this.paramBuilder( 0, this.defaultPageSize));
    this.findAllDashboards(this.paramBuilder( 0, this.defaultPageSize));
  }

  paramBuilder(page: number, pageSize: string): HttpParams {
    const query = (this.queryField.value) ? this.queryField.value : '';
    return new HttpParams()
      .set('page', page.toString())
      .set('size', pageSize)
      .set('search', query)
      .set('type', this.dashboardType);
  }
}
