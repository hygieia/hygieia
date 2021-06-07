import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { OSSDetailComponent } from './oss-detail.component';
import {NgbActiveModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {DashStatus} from '../../../shared/dash-status/DashStatus';
import {ReactiveFormsModule} from '@angular/forms';
import {SharedModule} from '../../../shared/shared.module';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {OpensourceScanModule} from '../opensource-scan.module';

describe('OSSDetailComponent', () => {
  let component: OSSDetailComponent;
  let fixture: ComponentFixture<OSSDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [OpensourceScanModule, ReactiveFormsModule, NgbModule, SharedModule, HttpClientTestingModule],
      declarations: [ ],
      providers: [ NgbActiveModal ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OSSDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call ngOnInit()', () => {
    component.ngOnInit();
  });

  it('should set detailData', () => {
    const detailData = {
      status: DashStatus.PASS,
      statusText: 'oss.level',
      title: 'ossStatusTitle',
      subtitles: [],
      url: 'reportUrl',
      components: [],
      lastUpdated: 1587131351
    };
    component.detailData = detailData;
    expect(component.data).toEqual(detailData);

    component.detailData = null;
    expect(component.data).toEqual(null);
  });

});
