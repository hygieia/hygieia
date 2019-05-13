import { NgModule } from '@angular/core';
import { TestDashComponent } from './test-dash/test-dash.component';
import {RouterModule, Routes} from '@angular/router';

const routes: Routes = [
  {
    path: '',
    component: TestDashComponent,
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TestDashboardRoutingModule {
  static components = [TestDashComponent];
}

