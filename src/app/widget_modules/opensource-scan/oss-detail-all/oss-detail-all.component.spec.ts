import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OSSDetailAllComponent } from './oss-detail-all.component';
import {DashStatus} from '../../../shared/dash-status/DashStatus';
import {NgbActiveModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {ReactiveFormsModule} from '@angular/forms';
import {SharedModule} from '../../../shared/shared.module';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {IClickListData, IClickListItemOSS} from '../../../shared/charts/click-list/click-list-interfaces';
import {OSSDetailComponent} from '../oss-detail/oss-detail.component';
import {OpensourceScanModule} from '../opensource-scan.module';

describe('OSSDetailAllComponent', () => {
  let component: OSSDetailAllComponent;
  let fixture: ComponentFixture<OSSDetailAllComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [OpensourceScanModule, ReactiveFormsModule, NgbModule, SharedModule, HttpClientTestingModule],
      declarations: [ ],
      providers: [NgbActiveModal]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OSSDetailAllComponent);
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
    const latestDetails = {
      status: DashStatus.PASS,
      statusText: 'oss.level',
      title: 'ossStatusTitle',
      subtitles: [],
      url: 'reportUrl',
      components: [],
      lastUpdated: 1587131399
    } as IClickListItemOSS;

    const detailData = {
      items: [latestDetails],
      clickableContent: OSSDetailComponent,
      clickableHeader: OSSDetailAllComponent
    } as IClickListData;

    component.detailData = detailData;
    expect(component.data[0]).toEqual(latestDetails);

    component.detailData = null;
    expect(component.data).toEqual(null);
  });
});
