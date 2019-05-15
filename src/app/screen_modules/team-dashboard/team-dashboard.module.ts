import { NgModule } from '@angular/core';

import { BuildWidgetComponent} from '../../widget_modules/build/build-widget/build-widget.component';
import { DashboardViewComponent } from './dashboard-view/dashboard-view.component';
import { SharedModule } from '../../shared/shared.module';
import { TeamDashboardRoutingModule } from './team-dashboard-routing.module';
import { TestDashboardRoutingModule } from './test-dashboard-routing-module';
import { TestDashComponent } from './test-dash/test-dash.component';
import {WidgetHeaderComponent} from '../../shared/widget-header/widget-header.component';

@NgModule({
  declarations: [
    // BuildWidgetComponent,
    DashboardViewComponent,
    TestDashComponent
  ],
  imports: [
    SharedModule,
    TeamDashboardRoutingModule,
    TestDashboardRoutingModule
  ],
  entryComponents: [
    BuildWidgetComponent,
    // WidgetHeaderComponent
  ]
})
export class TeamDashboardModule { }
