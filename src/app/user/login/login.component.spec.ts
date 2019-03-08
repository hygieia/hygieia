import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { LoginComponent } from './login.component';
import {AuthService} from '../../core/services/auth.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let router: Router;
  let authService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([]),
        HttpClientModule
      ],
      declarations: [ LoginComponent ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.get(AuthService);
    router = TestBed.get(Router);
    fixture.detectChanges();
  });
  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('username input should exist', () => {
    const field = fixture.debugElement.query(By.css('#username'));
    expect(field).toBeTruthy();
  });
  it('password input should exist', () => {
    const field = fixture.debugElement.query(By.css('#password'));
    expect(field).toBeTruthy();
  });
  it('should tell ROUTER to navigate when sign up clicked', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.signUp();
    expect(navigateSpy).toHaveBeenCalledWith(['/user/signup']);
  });
  it('should be standard login', () => {
    component.activeTab = 'STANDARD';
    expect(component.isStandLogin).toBeTruthy();
  });
  it('should be ldap login', () => {
    component.activeTab = 'LDAP';
    expect(component.isLdapLogin).toBeTruthy();
  });
  it('should login with STANDARD user', () => {
    component.activeTab = 'STANDARD';
    const spy = spyOn(authService, 'login').and.returnValue({ subscribe: () => true });
    const obj = { value : {
        username : 'test',
        password: 'test'
      }};
    component.submit(obj);
    expect(spy).toHaveBeenCalledWith({username: 'test', password: 'test'});
  });
  it('should login with LDAP user', () => {
    component.activeTab = 'LDAP';
    const spy = spyOn(authService, 'loginLdap').and.returnValue({ subscribe: () => true });
    const obj = { value : {
        username : 'test',
        password: 'test'
      }};
    component.submit(obj);
    expect(spy).toHaveBeenCalledWith({username: 'test', password: 'test'});
  });
});

