import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OSSConfigFormComponent } from './oss-config-form.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {NgbActiveModal, NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {SharedModule} from '../../../shared/shared.module';
import {ReactiveFormsModule} from '@angular/forms';
import {CollectorService} from '../../../shared/collector.service';
import {DashboardService} from '../../../shared/dashboard.service';
import {Observable, of} from 'rxjs';
import {OpensourceScanModule} from '../opensource-scan.module';

class MockCollectorService {
  mockCollectorData = {
    id: '4321',
    description: 'LB : scan1',
    collectorId: '1234',
    collector: {
      id: '1234',
      name: 'LB',
      collectorType: 'LibraryPolicy'
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
          LibraryPolicy: [{
            id: '1234',
            description: 'LB : scan1'
          }]
        }
      }]
    }
  };
  dashboardConfig$ = of(this.mockDashboard);
}

describe('OSSConfigFormComponent', () => {
  let component: OSSConfigFormComponent;
  let fixture: ComponentFixture<OSSConfigFormComponent>;
  let dashboardService: DashboardService;
  let collectorService: CollectorService;
  let modalService: NgbModule;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [OpensourceScanModule, ReactiveFormsModule, NgbModule, SharedModule, HttpClientTestingModule],
      declarations: [ ],
      providers: [{ provide: NgbActiveModal, useClass: NgbActiveModal },
        { provide: CollectorService, useClass: MockCollectorService},
        { provide: DashboardService, useClass: MockDashboardService}]
    })
      .compileComponents();

  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OSSConfigFormComponent);
    component = fixture.componentInstance;
    dashboardService = TestBed.get(DashboardService);
    collectorService = TestBed.get(CollectorService);
    modalService = TestBed.get(NgbModal);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should getOssTitle', () => {
    const collectorItem = {
      description : 'example-oss',
      niceName : 'example',
    };
    expect(component.getOssTitle(collectorItem)).toEqual('example-oss');
    expect(component.getOssTitle(null)).toEqual('');
  });

  it('should set widgetConfig', () => {
    const widgetConfigData = {
      options: {
        id: 788,
      }
    };
    component.widgetConfig = widgetConfigData;
    component.widgetConfig = null;
  });

  it('should call ngOnInit()', () => {
    component.ngOnInit();
  });

  it('should getOssTitle', () => {
    const collectorItem = {
      description : 'example-oss'
    };
    expect(component.getOssTitle(collectorItem)).toEqual('example-oss');
    expect(component.getOssTitle(null)).toEqual('');
  });

  it('should assign selected job after submit', () => {
    component.createForm();
    expect(component.ossConfigForm.get('ossJob').value).toEqual('');
    component.ossConfigForm = component.formBuilder.group({ossJob: 'ossJob1'});
    component.submitForm();
    expect(component.ossConfigForm.get('ossJob').value).toEqual('ossJob1');
  });

  it('should load saved oss job', () => {
    component.loadSavedOssJob();
    collectorService.getItemsById('4321').subscribe(result => {
      expect(component.ossConfigForm.get('ossJob').value).toEqual(result);
    });
  });

});
