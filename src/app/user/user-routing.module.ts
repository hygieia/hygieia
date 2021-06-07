import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { UserComponent } from './user.component';
import { SignupComponent } from './signup/signup.component';
import {SsoComponent} from './sso/sso.component';
import {LoginComponent} from './login/login.component';
const routes: Routes = [
  {
    path: '',
    component: UserComponent,
    children: [
      { path: 'login', component: LoginComponent },
      { path: 'signup', component: SignupComponent },
      { path: 'sso', component: SsoComponent },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserRoutingModule {
  static components = [UserComponent, LoginComponent, SignupComponent, SsoComponent];
}
