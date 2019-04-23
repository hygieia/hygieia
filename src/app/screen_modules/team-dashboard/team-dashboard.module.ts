import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { TeamDashboardRoutingModule } from './team-dashboard-routing.module';
import { DashboardViewComponent } from './dashboard-view/dashboard-view.component';

@NgModule({
  declarations: [DashboardViewComponent],
  imports: [
    SharedModule,
    TeamDashboardRoutingModule
  ],
  providers: []
})
export class TeamDashboardModule { }
