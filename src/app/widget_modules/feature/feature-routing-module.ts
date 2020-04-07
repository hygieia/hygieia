import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FeatureWidgetComponent } from './feature-widget/feature-widget.component';

const routes: Routes = [
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FeatureRoutingModule {
  static components = [FeatureWidgetComponent];
}
