import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {SecurityScanWidgetComponent} from './security-scan-widget/security-scan-widget.component';

const routes: Routes = [];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SecurityScanRoutingModule {
  static components = [SecurityScanWidgetComponent];
}
