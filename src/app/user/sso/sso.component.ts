import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../../../src/environments/environment';
import {SsoService} from './sso.service';

@Component({
  selector: 'app-sso',
  templateUrl: './sso.component.html',
  styleUrls: ['./sso.component.scss'],
  providers: [ SsoService ],
})

export class SsoComponent implements OnInit {

  public status = 'Verifying identity...';

  constructor(private router: Router, private ssoService: SsoService) { }

  ngOnInit() {
    if (environment.authorization.ALLOW_SSO) {
      if (!localStorage.getItem('auth-code')) {
        this.ssoService.obtainAuthCode();
      } else {
        this.ssoService.callSsoLogin();
      }
    } else {
      this.router.navigateByUrl('/');
    }

    setTimeout(() => {
      this.status = 'Your session has timed out, please re-login!';
    }, 5000);
  }
}
