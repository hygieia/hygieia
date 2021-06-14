import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardCreateComponent } from './dashboard-create.component';
import {NbButtonModule, NbDialogModule, NbDialogRef, NbInputModule, NbThemeModule} from '@nebular/theme';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {NgbTypeaheadModule} from '@ng-bootstrap/ng-bootstrap';
import { DashboardService } from 'src/app/shared/dashboard.service';
import { Observable, of } from 'rxjs';
import { Router } from '@angular/router';

class MockDialogRef {}

class MockRouter {
  navigate() {}
}

class MockDashboardService {
  createDashboard: () => {}
}

describe('DashboardCreateComponent', () => {
  let component: DashboardCreateComponent;
  let fixture: ComponentFixture<DashboardCreateComponent>;
  let service: DashboardService;
  let router: Router;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, NbThemeModule.forRoot(), NbInputModule, NbButtonModule, HttpClientTestingModule,
        RouterTestingModule.withRoutes([]), NbDialogModule, NgbTypeaheadModule],
      declarations: [ DashboardCreateComponent ],
      providers: [
        { provide: NbDialogRef, useClass: MockDialogRef },
        { provide: Router, useClass: MockRouter }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardCreateComponent);
    component = fixture.componentInstance;
    service = TestBed.get(DashboardService);
    router = TestBed.get(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create dashboard', () => {
    const spy = spyOn(service, 'createDashboard').and.returnValue({subscribe: () => {}})
    component.createDashboard();
    expect(spy).toHaveBeenCalled();
  })
});
