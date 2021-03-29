import { Injectable, Output, EventEmitter } from '@angular/core';
import {HttpClient, HttpParams, HttpHeaders, HttpRequest} from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

// 3rd party import
import { JwtHelperService } from '@auth0/angular-jwt';

// local imports
import { IUserLogin } from '../../shared/interfaces';
import { IUser } from '../../shared/interfaces';

const helper = new JwtHelperService();
const httpOptions = {
  headers: new HttpHeaders({
    Accept: 'application/json;v=1',
    'Content-Type': 'application/x-www-form-urlencoded'
  })
};
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  redirectUrl: string;

  @Output() authChanged: EventEmitter<boolean> = new EventEmitter<boolean>();

  constructor(private http: HttpClient) {}

  getAuthenticationProviders(): any {
    const authenticationProvidersRoute = '/api/authenticationProviders';
    return this.http.get( authenticationProvidersRoute, httpOptions );
  }
  register(userLogin: IUserLogin): Observable<boolean> {
    return this.http.post(`/api/registerUser`, userLogin,
      { observe: 'response'})
      .pipe(
        map(loggedIn => {
          const token = loggedIn.headers.get('x-authentication-token');
          localStorage.setItem('access_token', token);
          if (token) {
            return true;
          }
        })
      );
  }
  loginLdap(userLogin: IUserLogin): Observable<boolean> {
    return this.callLogin(userLogin, `/api/login/ldap`);
  }

  login(userLogin: IUserLogin): Observable<boolean> {
    return this.callLogin(userLogin, `/api/login`);
  }

  private callLogin(userLogin: IUserLogin, path: string): Observable<boolean> {
    const params = new HttpParams({
      fromObject: {
        username: userLogin.username,
        password: userLogin.password,
      }
    });
    return this.http.post<boolean>( path,
      params,
      { headers: {'Content-Type': 'application/x-www-form-urlencoded'}, observe: 'response'})
      .pipe(
        map(loggedIn => {
          const token = loggedIn.headers.get('x-authentication-token');
          localStorage.setItem('access_token', token);
          if (token) {
            return true;
          }
        })
      );
  }

  public getToken(): string {
    return localStorage.getItem('access_token');
  }

  logout() {
    localStorage.removeItem('access_token');
    localStorage.removeItem('auth-code');
  }

  public getUserName(): string {
    return this.getUser().sub;
  }

  public getExpiration(): number {
    return this.getUser().exp;
  }

  public isAdmin(): boolean {
    const user = this.getUser();
    return user.roles && user.roles.indexOf('ROLE_ADMIN') > -1;
  }

  public getAuthType(): string {
    return this.getUser().details;
  }

  public isAuthenticated(): boolean {
    if (!helper.isTokenExpired( this.getToken() ) && this.getUserName() ) {
      return true;
    }
    return false;
  }

  private getUser(): IUser {
    const token = this.getToken();
    if (token) {
      return helper.decodeToken(this.getToken());
    } else {
      return {} as IUser;
    }
  }

  getAuthCode(): string {
    return localStorage.getItem('auth-code');
  }

  isSsoLogin(req: HttpRequest<any>): boolean {
    return (req.url === '/api/login/openid' && !!this.getAuthCode());
  }
}
