import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminDashboardComponent } from './admin-dashboard.component';
import { GenerateApiTokensComponent } from './generate-api-tokens/generate-api-tokens.component';
import { ManageAdminsComponent } from './manage-admins/manage-admins.component';
import { FormsModule, ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AdminFilterPipe } from '../../pipes/filter.pipe';
import { DashEditComponent } from './dash-edit/dash-edit.component';
import { DashTrashComponent } from './dash-trash/dash-trash.component';
import { AdminOrderByPipe } from '../../pipes/order-by.pipe';
import {FeatureFlagsComponent} from './feature-flags/feature-flags.component';
import {ServiceAccountsComponent} from './service-accounts/service-accounts.component';
import {UserDataService} from '../../services/user-data.service';
import { NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import { EditDashboardComponent } from './edit-dashboard/edit-dashboard.component';
import { DashboardDataService } from '../../services/dashboard-data.service';
import { CmdbDataService } from '../../services/cmdb-data.service';
import { AdminDashboardService } from '../../services/dashboard.service';
import { PaginationWrapperService } from '../../services/pagination-wrapper.service';
import { SharedModule } from 'src/app/shared/shared.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import {NbThemeModule} from '@nebular/theme';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {NbEvaIconsModule} from '@nebular/eva-icons';

describe('AdminDashboardComponent', () => {
    let component: AdminDashboardComponent;
    let fixture: ComponentFixture<AdminDashboardComponent>;
    beforeEach(async(() => {
      TestBed.configureTestingModule({
          declarations: [AdminDashboardComponent, GenerateApiTokensComponent, ManageAdminsComponent, EditDashboardComponent,
               AdminOrderByPipe, AdminFilterPipe, DashTrashComponent, DashEditComponent, FeatureFlagsComponent,
               ServiceAccountsComponent ],
          imports: [FormsModule, CommonModule, ReactiveFormsModule, SharedModule, HttpClientTestingModule,
            RouterTestingModule.withRoutes([]), NbThemeModule.forRoot(), NbEvaIconsModule],
          providers: [DashboardDataService,
              CmdbDataService,
              AdminDashboardService,
              PaginationWrapperService, FormBuilder, NgbActiveModal, UserDataService ],
        schemas: [CUSTOM_ELEMENTS_SCHEMA]
      })
          .compileComponents();
  }));

    beforeEach(() => {
        fixture = TestBed.createComponent(AdminDashboardComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
