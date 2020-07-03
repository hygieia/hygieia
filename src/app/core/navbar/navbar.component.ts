import { Component, OnInit, Input } from '@angular/core';
import { Router } from '@angular/router';

// local imports
import { AuthService } from '../services/auth.service';
@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {

  @Input() isAdminLoad = true;

  constructor(private router: Router,
              private auth: AuthService) {}

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
    this.isAdminLoad = true;
    this.router.navigate(['/user/login']);
  }

  get isAdmin(): boolean {
     return this.auth.isAdmin();
  }

  admin() {

    this.isAdminLoad = false;
    this.router.navigate(['/admin/dashboard']);
  }

  openGithub() {
    window.open('https://hygieia.github.io/Hygieia/getting_started.html', '_blank');
  }
}
