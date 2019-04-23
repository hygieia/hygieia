import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { TestDashComponent } from './test-dash/test-dash.component';

import { TeamDashRoutingModule } from './team-dashboard-routing-module';
import { BuildWidgetComponent} from '../../widget_modules/build/build-widget/build-widget.component';

@NgModule({
  declarations: [
    BuildWidgetComponent,
    TestDashComponent
  ],
  imports: [
    SharedModule,
    TeamDashRoutingModule
  ],
  entryComponents: [
    BuildWidgetComponent
  ]
})
export class TeamDashboardModule { }
