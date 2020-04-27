import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {OSSWidgetComponent} from './oss-widget/oss-widget.component';

const routes: Routes = [
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class OpensourceScanRoutingModule {
  static components = [OSSWidgetComponent];
}
