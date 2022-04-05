import { Component, OnInit } from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';

// local imports
import {AuthService} from '../../core/services/auth.service';
import {IUserLogin} from '../../shared/interfaces';

function passwordMatcher(c: AbstractControl): { [key: string]: boolean } | null {
  const passwordControl = c.get('password');
  const confirmControl = c.get('confirmPassword');
  if (passwordControl.pristine || confirmControl.pristine) {
    return null;
  }

  if (passwordControl.value === confirmControl.value) {
    return null;
  }
  return { match: true };
}
@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent implements OnInit {
  signupForm: FormGroup;
  errorMessage: string;

  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private authService: AuthService) { }

  ngOnInit() {
    this.buildForm();
    this.authService.logout();
  }
  buildForm() {
    this.signupForm = this.formBuilder.group({
      username:      ['', [ Validators.required ]],
      passwordGroup: this.formBuilder.group({
        password: ['', [Validators.required ]],
        confirmPassword: ['', Validators.required],
      }, { validator: passwordMatcher }),
    });
  }
  submit({ value }) {
    const data: IUserLogin = {
      username: value.username,
      password: value.passwordGroup.password
    };
    this.authService.register(data).subscribe((status: boolean) => {
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
        this.errorMessage = 'User already exists';
      });
  }
  login() {
    this.router.navigate(['/user/login']);
  }
}
