import { NgModule } from '@angular/core';

import { BuildWidgetComponent} from '../../widget_modules/build/build-widget/build-widget.component';
import { DashboardViewComponent } from './dashboard-view/dashboard-view.component';
import { SharedModule } from '../../shared/shared.module';
import { TeamDashboardRoutingModule } from './team-dashboard-routing.module';
import {WidgetHeaderComponent} from '../../shared/widget-header/widget-header.component';

@NgModule({
  declarations: [
    // BuildWidgetComponent,
    DashboardViewComponent
  ],
  imports: [
    SharedModule,
    TeamDashboardRoutingModule
  ],
  entryComponents: [
    BuildWidgetComponent,
    // WidgetHeaderComponent
  ]
})
export class TeamDashboardModule { }
