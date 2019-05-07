import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing/';
import {RouterTestingModule} from '@angular/router/testing';
import {Router} from '@angular/router';

import { SharedModule } from '../shared/shared.module';
import { LandingPageComponent } from './landing-page.component';
import { DashboardListComponent } from './dashboard-list/dashboard-list.component';
import { DashboardListService } from './dashboard-list/dashboard-list.service';


describe('LandingPageComponent', () => {
  let component: LandingPageComponent;
  let fixture: ComponentFixture<LandingPageComponent>;
  let router: Router;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, SharedModule, HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ LandingPageComponent, DashboardListComponent ],
      providers: [DashboardListService]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LandingPageComponent);
    component = fixture.componentInstance;
    router = TestBed.get(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
