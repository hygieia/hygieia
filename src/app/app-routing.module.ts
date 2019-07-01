import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  { path: '', loadChildren: () => import('./landing_page/landing-page.module').then(m => m.LandingPageModule) },
  { path: 'user', loadChildren: () => import('./user/user.module').then(m => m.UserModule) },
  { path: 'build', loadChildren: () => import('./widget_modules/build/build.module').then(m => m.BuildModule) },
  { path: 'dashboard', loadChildren: () => import('./screen_modules/team-dashboard/team-dashboard.module').then(m => m.TeamDashboardModule) }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
