import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  { path: '', loadChildren: './landing_page/landing-page.module#LandingPageModule' },
  { path: 'user', loadChildren: './user/user.module#UserModule' },
  { path: 'build', loadChildren: './widget_modules/build/build.module#BuildModule' },
  { path: 'dashboard', loadChildren: './screen_modules/team-dashboard/team-dashboard.module#TeamDashboardModule' },
  { path: 'test-dash',  loadChildren: './screen_modules/team-dashboard/team-dashboard.module#TeamDashboardModule'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
