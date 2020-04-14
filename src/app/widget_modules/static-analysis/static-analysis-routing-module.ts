import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { StaticAnalysisWidgetComponent } from './static-analysis-widget/static-analysis-widget.component';

const routes: Routes = [
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class StaticAnalysisRoutingModule {
  static components = [StaticAnalysisWidgetComponent];
}
