import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { TeamDashboardRoutingModule } from './team-dashboard-routing.module';
import { DashboardViewComponent } from './dashboard-view/dashboard-view.component';
import {DashboardListService} from '../../landing_page/dashboard-list/dashboard-list.service';
import {DashboardViewService} from './dashboard-view/dashboard-view.service';

@NgModule({
  declarations: [DashboardViewComponent],
  imports: [
    SharedModule,
    TeamDashboardRoutingModule
  ],
  providers: [ DashboardViewService ]
})
export class TeamDashboardModule { }
