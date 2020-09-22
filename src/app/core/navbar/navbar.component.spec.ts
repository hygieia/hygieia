import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { NavbarComponent } from './navbar.component';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {NbThemeModule} from '@nebular/theme';

describe('NavbarComponent', () => {
  let component: NavbarComponent;
  let fixture: ComponentFixture<NavbarComponent>;
  let router: Router;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([]),
        HttpClientTestingModule,
        NbThemeModule.forRoot()
      ],
      declarations: [ NavbarComponent ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NavbarComponent);
    component = fixture.componentInstance;
    router = TestBed.get(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should tell ROUTER to navigate when Login/Logout clicked', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.loginOrOut();
    expect(navigateSpy).toHaveBeenCalledWith(['/user/login']);
  });
  it('should get userName', () => {
    const userTest = component.userName;
    expect(userTest).not.toBeNull();
  });
  it('should get danger status for login icon', () => {
    expect(component.customPowerIcon.status).toBe('danger');
    expect(component.customPowerIcon.icon).toBe('power-outline');
  });
});
