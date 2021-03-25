import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { BuildWidgetComponent } from './build-widget/build-widget.component';
import { BuildDetailPageComponent } from './build-detail-page/build-detail-page.component';
import { BuildViewerComponent } from './build-viewer/build-viewer.component';


const routes: Routes = [
  { path: ':id', component: BuildDetailPageComponent },
  { path: 'viewer', component: BuildViewerComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BuildRoutingModule {
  static components = [BuildWidgetComponent];
}
