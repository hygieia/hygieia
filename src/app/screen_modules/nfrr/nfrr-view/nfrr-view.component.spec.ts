import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NfrrViewComponent } from './nfrr-view.component';
import {NgxChartsModule} from '@swimlane/ngx-charts';
import {RouterModule} from '@angular/router';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {IAudit} from '../../../shared/interfaces';
import {Observable, of} from 'rxjs';
import {NfrrService} from '../nfrr.service';


class MockNfrrService {
  mockNfrrData1 = [{lineOfBusiness: 'lob1', auditType: 'TEST_RESULT', auditTypeStatus: 'OK', auditStatus: 'FAIL', timestamp: 158439824},
    {lineOfBusiness: 'lob2', auditType: 'PERF_TEST', auditTypeStatus: 'OK', auditStatus: 'OK', timestamp: 1584396876}];
  mockNfrrData2 = [{lineOfBusiness: 'lob1', auditType: 'TEST_RESULT', auditTypeStatus: 'OK', auditStatus: 'FAIL', timestamp: 158439824}];

  getAuditMetricsAll(): Observable<IAudit[]> {
    return of(this.mockNfrrData1 as IAudit[]);
  }
  getAuditMetricsByLob(lob): Observable<IAudit[]> {
    return of(this.mockNfrrData2 as IAudit[]);
  }
}

describe('NfrrViewComponent', () => {
  let component: NfrrViewComponent;
  let fixture: ComponentFixture<NfrrViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [NgxChartsModule, HttpClientTestingModule, RouterModule.forRoot([]), BrowserAnimationsModule],
      declarations: [ NfrrViewComponent ],
      schemas: [ NO_ERRORS_SCHEMA ],
      providers: [{provide: NfrrService, useClass: MockNfrrService}],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NfrrViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get valid audit type name', () => {
    expect(component.auditTypeToReadable('LIBRARY_POLICY') === 'Open Source');
  });

  it('should transform to chart data', () => {
    const audits: IAudit[] = [
      {lineOfBusiness: 'lob1', auditType: 'TEST_RESULT', auditTypeStatus: 'OK', auditStatus: 'FAIL', timestamp: 158439824},
      {lineOfBusiness: 'lob2', auditType: 'PERF_TEST', auditTypeStatus: 'OK', auditStatus: 'OK', timestamp: 1584396876}];
    component.transformToChartData(audits);
    expect(component.ngxData.data[0].name).toEqual('TEST_RESULT');
    expect(component.ngxData.data[1].name).toEqual('PERF_TEST');
  });

  it('should chart all audit data', () => {
    component.getAuditMetricsAll();
    expect(component.ngxData.data.length).toEqual(2);
  });

  it('should chart audit data by lob', () => {
    component.getAuditMetricsByLob('lob1');
    expect(component.ngxData.data.length).toEqual(1);
  });
});
