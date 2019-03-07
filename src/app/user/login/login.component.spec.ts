import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let router: Router;

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
    router = TestBed.get(Router);
    component = fixture.componentInstance;
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
  xit('should login with existing user', () => {
  });
});

