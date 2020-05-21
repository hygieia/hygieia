import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';

import { LandingPageRoutingModule } from './landing-page-routing.module';
import { DashboardListService } from './dashboard-list/dashboard-list.service';
import { DashboardListComponent } from './dashboard-list/dashboard-list.component';

@NgModule({
  declarations: [
    LandingPageRoutingModule.components,
    DashboardListComponent,

  ],
  imports: [
    SharedModule,
    LandingPageRoutingModule,

  ],
  providers: [ DashboardListService ]
})
export class LandingPageModule { }
