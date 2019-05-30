import { NgModule } from '@angular/core';

import { DashboardViewComponent } from './dashboard-view/dashboard-view.component';
import { SharedModule } from '../../shared/shared.module';
import { TeamDashboardRoutingModule } from './team-dashboard-routing.module';

@NgModule({
  declarations: [
    DashboardViewComponent
  ],
  imports: [
    SharedModule,
    TeamDashboardRoutingModule
  ],
  entryComponents: [
  ]
})
export class TeamDashboardModule { }
