import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { StaticAnalysisDetailComponent } from './static-analysis-detail.component';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {TimeAgoPipe} from 'time-ago-pipe';

describe('StaticAnalysisDetailComponent', () => {
  let component: StaticAnalysisDetailComponent;
  let fixture: ComponentFixture<StaticAnalysisDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StaticAnalysisDetailComponent, TimeAgoPipe ],
      providers: [ NgbActiveModal ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StaticAnalysisDetailComponent);
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
      name: 'sonar-project-1',
      timestamp: 1552590574305,
      url: 'https://sonar.com',
      items: [],
      clickableContent: null,
      clickableHeader: null,
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
