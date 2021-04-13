import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable()
export class SsoAuthGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate() {
    if (!document.baseURI.includes('localhost')) {
      if (!localStorage.getItem('access_token')) {
        this.router.navigateByUrl('/user/sso');
      }
    }
    return true;
  }
}
