import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(
    private authService: AuthService,
    private router: Router) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (this.authService.getToken()) {
      const authReq = req.clone({headers: req.headers.set('Authorization', `Bearer ${this.authService.getToken()}`)});
      return next.handle(authReq);
    } else if (this.authService.isSsoLogin(req)) {
      const authReq = req.clone({headers: req.headers.set('Authorization', `ssoCode ${this.authService.getAuthCode()}`)});
      return next.handle(authReq);
    }
    return next.handle(req).pipe(catchError((err: any) => {
      if (err instanceof HttpErrorResponse) {
        if (err.status === 401) {
          this.router.navigate(['/user/login']);
          return throwError(err);
        }
      }
      return throwError(err);
    }));
  }
}
