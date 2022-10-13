import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DashboardViewComponent } from './dashboard-view/dashboard-view.component';

const routes: Routes = [
  { path: 'dashboard-view/:id', component: DashboardViewComponent }
];

@NgModule({
  declarations: [],
  imports: [RouterModule.forChild(routes), CommonModule],
  exports: [RouterModule, CommonModule]
})
export class TeamDashboardRoutingModule {
  static components = [DashboardViewComponent];

}
