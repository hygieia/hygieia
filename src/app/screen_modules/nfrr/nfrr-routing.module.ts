import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {NfrrViewComponent} from './nfrr-view/nfrr-view.component';

const routes: Routes = [
  {
    path: 'nfrr',
    component: NfrrViewComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes), CommonModule],
  exports: [RouterModule, CommonModule]
})
export class NfrrRoutingModule {
  static components: [NfrrViewComponent];
}
