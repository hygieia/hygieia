import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

// local imports
import { LandingPageComponent } from './landing-page.component';

const routes: Routes = [
  {
    path: '',
    component: LandingPageComponent
  }
];

@NgModule({
  imports: [ RouterModule.forChild(routes), CommonModule ],
  exports: [ RouterModule ]
})
export class LandingPageRoutingModule {
  static components = [LandingPageComponent];
}
