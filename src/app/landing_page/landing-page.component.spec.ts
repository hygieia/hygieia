import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing/';

import { SharedModule } from '../shared/shared.module';
import { LandingPageComponent } from './landing-page.component';
import { DashboardListComponent } from './dashboard-list/dashboard-list.component';
import { DashboardListService } from './dashboard-list/dashboard-list.service';

describe('LandingPageComponent', () => {
  let component: LandingPageComponent;
  let fixture: ComponentFixture<LandingPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, SharedModule, HttpClientTestingModule],
      declarations: [ LandingPageComponent, DashboardListComponent ],
      providers: [DashboardListService]
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
