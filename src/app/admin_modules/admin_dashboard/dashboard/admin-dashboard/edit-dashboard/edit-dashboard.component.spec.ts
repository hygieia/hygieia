import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { EditDashboardComponent } from './edit-dashboard.component';
import { AdminDashboardService } from '../../../services/dashboard.service';
import { PaginationWrapperService } from '../../../services/pagination-wrapper.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NgModule } from '@angular/core';
import { SharedModule } from 'src/app/shared/shared.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { HttpClientModule } from '@angular/common/http';
import { MockDashboardDataService } from '../../../services/mock-dashboard-data.service';
import { MockPaginationWrapperService } from '../../../services/mock-pagination-wrapper.service';
import {DASHBOARDITEM} from '../../../../../shared/services/user-data.service.mockdata';
import {DashboardItem} from '../../../../../shared/model/dashboard-item';
import {DashboardDataService} from '../../../../../shared/services/dashboard-data.service';

@NgModule({
  declarations: [EditDashboardComponent],
  providers: [{ provide: DashboardDataService, useClass: MockDashboardDataService },
    AdminDashboardService, { provide: PaginationWrapperService, useClass: MockPaginationWrapperService }],
  imports: [FormsModule, CommonModule, NgbModule, ReactiveFormsModule, HttpClientTestingModule, SharedModule, HttpClientModule],
  entryComponents: []
})
class TestModule { }

describe('EditDashboardComponent', () => {
  let component: EditDashboardComponent;
  let fixture: ComponentFixture<EditDashboardComponent>;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [TestModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });


  it('should openConfig edit  when clicked', () => {
    component.editDashboard(DASHBOARDITEM as DashboardItem);
  });

  it('should openConfig delete  when clicked', () => {
    component.deleteDashboard(DASHBOARDITEM);
  });

  it('should getNextPage ', () => {
    component.getNextPage({page: 1 , pageSize: 10}, false);
  });

});
