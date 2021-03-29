import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
// local imports
import { AuthService } from '../../core/services/auth.service';
import { IUserLogin } from '../../shared/interfaces';
import {of} from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  errorMessage: string;
  authName: string;
  activeTab: string;
  authenticationProviders: Array<string>;

  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private authService: AuthService) {}

  ngOnInit() {
    this.authService.logout();
    this.buildForm();
    this.getAuthProviders();
  }
  getAuthProviders(): void {
    this.authService.getAuthenticationProviders().subscribe( data => {
      this.setActiveTab(data[0]);
      this.authenticationProviders = data;
    });
  }
  isStandLogin(): boolean {
    return this.activeTab === 'STANDARD';
  }
  isLdapLogin(): boolean {
    return this.activeTab === 'LDAP';
  }
  isSsoLogin(): boolean {
    return this.activeTab === 'SSO';
  }
  setActiveTab(tab: string) {
    if ( tab ) {
      this.activeTab = tab;
      this.authName = tab;
    }
  }
  buildForm() {
    this.loginForm = this.formBuilder.group({
      username:      ['', [ Validators.required ]],
      password:   ['', [ Validators.required ]]
    });
  }

  submit({ value }: { value: IUserLogin }) {
    let login;
    if (this.isStandLogin()) {
      login = this.authService.login(value);
    } else if (this.isLdapLogin()) {
      login = this.authService.loginLdap(value);
    } else if (this.isSsoLogin()) {
      login = this.routeToSso();
    } else {
      login = of(false);
    }
    login.subscribe((status: boolean) => {
          if (status) {
            if (this.authService.redirectUrl) {
              const redirectUrl = this.authService.redirectUrl;
              this.authService.redirectUrl = '';
              this.router.navigate([redirectUrl]);
            } else {
              this.router.navigate(['/']);
            }
          }
        },
        (err: any) => {
          this.errorMessage = err.error.message;
        });
  }
  signUp() {
    this.router.navigate(['/user/signup']);
  }

  loginBtnName(): string {
    return this.isSsoLogin() ? 'Single Sign On' : 'Login';
  }

  routeToSso(): Promise<boolean> {
    return this.router.navigate(['/user/sso']);
  }
}

