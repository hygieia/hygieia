import { HttpClientTestingModule } from '@angular/common/http/testing/';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '../shared/shared.module';
import { DashboardListComponent } from './dashboard-list/dashboard-list.component';
import { DashboardListService } from './dashboard-list/dashboard-list.service';
import { LandingPageComponent } from './landing-page.component';
import {NbDialogService, NbThemeModule} from '@nebular/theme';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';

class MockDialogService {}

describe('LandingPageComponent', () => {
  let component: LandingPageComponent;
  let fixture: ComponentFixture<LandingPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, SharedModule, HttpClientTestingModule, RouterTestingModule.withRoutes([]),
        NbThemeModule.forRoot()],
      declarations: [ LandingPageComponent, DashboardListComponent ],
      providers: [DashboardListService, {provide: NbDialogService, useClass: MockDialogService}],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LandingPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
