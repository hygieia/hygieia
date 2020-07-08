import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { SecurityScanDeleteFormComponent } from './security-scan-delete-form.component';
import {ReactiveFormsModule} from '@angular/forms';
import {NgbActiveModal, NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {Observable, of} from 'rxjs';
import {CollectorService} from '../../../shared/collector.service';
import {NgModule, NO_ERRORS_SCHEMA} from '@angular/core';
import {SharedModule} from '../../../shared/shared.module';
import {CommonModule} from '@angular/common';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {RouterModule} from '@angular/router';
import {DashboardService} from '../../../shared/dashboard.service';
import {SecurityScanModule} from '../security-scan.module';

class MockCollectorService {
  mockCollectorData = {
    id: '4321',
    description: 'Scanner : scan1',
    collectorId: '1234',
    collector: {
      id: '1234',
      name: 'Scanner',
      collectorType: 'StaticSecurityScan'
    }
  };

  getItemsById(id: string): Observable<any> {
    return of(this.mockCollectorData);
  }
}

class MockDashboardService {
  mockDashboard = {
    title: 'dashboard1',
    application: {
      components: [{
        collectorItems: {
          StaticSecurityScan: [{
            id: '1234',
            description: 'Scanner : scan1'
          }]
        }
      }]
    }
  };
  dashboardConfig$ = of(this.mockDashboard);
}

@NgModule({
  declarations: [],
  imports: [HttpClientTestingModule, SharedModule, CommonModule, BrowserAnimationsModule,
    RouterModule.forRoot([]), NgbModule, SecurityScanModule],
  entryComponents: []
})
class TestModule { }

describe('SecurityScanDeleteFormComponent', () => {
  let component: SecurityScanDeleteFormComponent;
  let fixture: ComponentFixture<SecurityScanDeleteFormComponent>;
  let dashboardService: DashboardService;
  let collectorService: CollectorService;
  let modalService: NgbModule;

  /*const secScanCollectorItem = {
    id: '1234',
    description: 'scan1',
    collectorId: '4321',
    collector: {
      id: '4321',
      name: 'Scanner',
      collectorType: 'StaticSecurityScan'
    }
  };*/

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [TestModule, HttpClientTestingModule, ReactiveFormsModule, NgbModule],
      declarations: [],
      providers: [
        { provide: NgbActiveModal, useClass: NgbActiveModal },
        { provide: CollectorService, useClass: MockCollectorService},
        { provide: DashboardService, useClass: MockDashboardService}],
      schemas: [NO_ERRORS_SCHEMA]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SecurityScanDeleteFormComponent);
    component = fixture.componentInstance;
    dashboardService = TestBed.get(DashboardService);
    collectorService = TestBed.get(CollectorService);
    modalService = TestBed.get(NgbModal);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set widgetConfig', () => {
    let widgetConfigData = {
      options: {
        id: 1234,
        sJob: '',
      }
    };
    component.widgetConfig = widgetConfigData;

    widgetConfigData = null;
    component.widgetConfig = widgetConfigData;
  });

  it('should call ngOnInit()', () => {
    component.ngOnInit();
  });

  it('should assign selected job after submit', () => {
    component.createDeleteForm();
    expect(component.securityDeleteForm.get('sJob').value).toEqual('');
    component.securityDeleteForm = component.formBuilder.group({sJob: 'secJob1'});
    component.submitDeleteForm();
    expect(component.securityDeleteForm.get('sJob').value).toEqual('secJob1');
  });

  it('should load saved security scan job', () => {
    component.getSavedSecurityJob();
    collectorService.getItemsById('4321').subscribe(result => {
      expect(component.securityDeleteForm.get('sJob').value).toEqual(result);
    });
  });
});
