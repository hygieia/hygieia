import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
    { path: '', loadChildren: './landing_page/landing-page.module#LandingPageModule' },
    { path: 'user', loadChildren: './user/user.module#UserModule' },
    { path: 'build', loadChildren: './widget_modules/build/build.module#BuildModule' }
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule { }
