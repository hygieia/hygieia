import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { of } from 'rxjs';

import { SignupComponent } from './signup.component';
import {AuthService} from '../../core/services/auth.service';

describe('SignupComponent', () => {
  let component: SignupComponent;
  let fixture: ComponentFixture<SignupComponent>;
  let router: Router;
  let registerSpy;

  beforeEach(async(() => {
    const authService = jasmine.createSpyObj('AuthService', ['register', 'logout']);
    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([]),
        HttpClientModule
      ],
      declarations: [ SignupComponent ],
      providers:    [ {provide: AuthService, useValue: authService } ]
    }).compileComponents();
    // Make the spy return a synchronous Observable with the test data
    registerSpy = authService.register.and.returnValue( of(true) );
    authService.logout.and.returnValue( of(true) );
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SignupComponent);
    component = fixture.componentInstance;
    router = TestBed.get(Router);

    fixture.detectChanges();
  });
  it('username input should exist', () => {
    const field = fixture.debugElement.query(By.css('#username'));
    expect(field).toBeTruthy();
  });
  it('password input should exist', () => {
    const field = fixture.debugElement.query(By.css('#password'));
    expect(field).toBeTruthy();
  });
  it('confirmPassword input should exist', () => {
    const field = fixture.debugElement.query(By.css('#confirmPassword'));
    expect(field).toBeTruthy();
  });
  it('should tell ROUTER to navigate when login clicked', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.login();
    expect(navigateSpy).toHaveBeenCalledWith(['/user/login']);
  });
  it('should register new user', () => {
    const obj = { value : {
      username : 'test',
      passwordGroup: { password: 'test' }
    }};
    component.submit(obj);
    expect(registerSpy).toHaveBeenCalledWith({username: 'test', password: 'test'});
  });
});
