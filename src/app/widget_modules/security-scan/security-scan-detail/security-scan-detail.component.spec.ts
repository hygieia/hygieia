import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SecurityScanDetailComponent } from './security-scan-detail.component';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {TimeAgoPipe} from 'time-ago-pipe';

describe('SecurityScanDetailComponent', () => {
  let component: SecurityScanDetailComponent;
  let fixture: ComponentFixture<SecurityScanDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SecurityScanDetailComponent, TimeAgoPipe ],
      providers: [ NgbActiveModal ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SecurityScanDetailComponent);
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
      clickableHeader: null,
      clickableContent: null,
      name: 'sast-project',
      items: [],
      timestamp: 1234566,
      url: 'test_url'
    };

    component.detailData = detailData;
    expect(component.data).toEqual(detailData);

    component.detailData = null;
    expect(component.data).toEqual(null);
  });

  it('should check isDate', () => {
    expect(component.isDate(new Date(1552590574305))).toBe(true);
  });
});
