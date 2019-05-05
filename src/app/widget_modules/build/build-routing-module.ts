import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { BuildWidgetComponent } from './build-widget/build-widget.component';

const routes: Routes = [
  {
    path: ':id',
    component: BuildWidgetComponent,
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BuildRoutingModule {
  static components = [BuildWidgetComponent];
}
