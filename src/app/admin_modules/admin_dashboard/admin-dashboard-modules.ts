import { NgModule } from '@angular/core';
import { AdminDashboardRoutingModule } from './admin-dashboard-routing';
import { AdminDashboardComponent } from './dashboard/admin-dashboard/admin-dashboard.component';
import { CommonModule } from '@angular/common';
import { GenerateApiTokensComponent } from './dashboard/admin-dashboard/generate-api-tokens/generate-api-tokens.component';
import { FormsModule } from '@angular/forms';
import { UserDataService } from './services/user-data.service';
import { HttpClientModule } from '@angular/common/http';
import { AdminFilterPipe } from './pipes/filter.pipe';
import { AdminOrderByPipe } from './pipes/order-by.pipe';
import { DashEditComponent } from './dashboard/admin-dashboard/dash-edit/dash-edit.component';
import { ManageAdminsComponent } from './dashboard/admin-dashboard/manage-admins/manage-admins.component';
import { EditTokenModalComponent } from './dashboard/admin-dashboard/modal/edit-token-modal/edit-token-modal.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
// tslint:disable-next-line:max-line-length
import { GenerateApiTokenModalComponent } from './dashboard/admin-dashboard/modal/generate-api-token-modal/generate-api-token-modal.component';
import { ReactiveFormsModule } from '@angular/forms';
import { DashTrashComponent } from './dashboard/admin-dashboard/dash-trash/dash-trash.component';
import { DeleteConfirmModalComponent } from './dashboard/admin-dashboard/modal/delete-confirm-modal/delete-confirm-modal.component';


@NgModule({
  declarations: [
    AdminDashboardComponent,
    GenerateApiTokensComponent,
    AdminFilterPipe,
    AdminOrderByPipe,
    DashEditComponent,
    ManageAdminsComponent,
    EditTokenModalComponent,
    GenerateApiTokenModalComponent,
    DashTrashComponent,
    DeleteConfirmModalComponent,
  ],

  providers: [UserDataService],

  imports: [
    AdminDashboardRoutingModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    ReactiveFormsModule
  ],
  entryComponents: [
    EditTokenModalComponent,
    GenerateApiTokenModalComponent,
    DeleteConfirmModalComponent
  ]
})
export class AdminDashboardModule { }
