import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardCountComponent } from './dashboard-count.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {DashboardService} from '../../../shared/dashboard.service';

describe('DashboardCountComponent', () => {
  let component: DashboardCountComponent;
  let fixture: ComponentFixture<DashboardCountComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DashboardCountComponent ],
      imports: [HttpClientTestingModule],
      providers: [DashboardService],
      schemas: [NO_ERRORS_SCHEMA]

    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardCountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
