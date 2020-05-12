import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

// local imports
import { AuthService } from '../services/auth.service';
@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {

  myadmin = false;

  constructor(private router: Router,
              private auth: AuthService) {
                this.myadmin = auth.isAdmin();
               }

  ngOnInit() {
  }
  get userName(): string {
    if (this.isAuthenticated ) {
      return this.auth.getUserName().toUpperCase();
    }
    return '';
  }
  get isAuthenticated(): boolean {
    return this.auth.isAuthenticated();
  }

  loginOrOut() {
    const isAuthenticated = this.isAuthenticated;
    if (isAuthenticated) {
      this.auth.logout();
    }
    this.redirectToLogin();
  }

  redirectToLogin() {
    this.router.navigate(['/user/login']);
  }

  admin() {
    this.myadmin = false;
    this.router.navigate(['/admin/dashboard']);
  }
}
