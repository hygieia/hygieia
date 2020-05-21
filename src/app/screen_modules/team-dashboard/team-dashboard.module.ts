import { NgModule } from '@angular/core';

import { DashboardViewComponent } from './dashboard-view/dashboard-view.component';
import { SharedModule } from '../../shared/shared.module';
import { TeamDashboardRoutingModule } from './team-dashboard-routing.module';
import { DasboardNavbarComponent } from 'src/app/core/dasboard-navbar/dasboard-navbar.component';

@NgModule({
  declarations: [
    DashboardViewComponent,
    DasboardNavbarComponent
  ],
  imports: [
    SharedModule,
    TeamDashboardRoutingModule
  ],
  entryComponents: [
  ]
})
export class TeamDashboardModule { }
