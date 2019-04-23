import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardViewComponent } from './dashboard-view.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {CommonModule} from '@angular/common';
import {SharedModule} from '../../../shared/shared.module';

describe('DashboardViewComponent', () => {
  let component: DashboardViewComponent;
  let fixture: ComponentFixture<DashboardViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DashboardViewComponent ],
      imports: [HttpClientTestingModule, SharedModule, CommonModule]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});



