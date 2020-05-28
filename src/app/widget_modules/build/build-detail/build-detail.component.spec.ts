import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BuildDetailComponent } from './build-detail.component';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

describe('BuildDetailComponent', () => {
  let component: BuildDetailComponent;
  let fixture: ComponentFixture<BuildDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BuildDetailComponent ],
      providers: [ NgbActiveModal ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BuildDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set detailData', () => {
    const detailData = [{
      title: 'buildTitle',
      url: 'buildUrl',
      lastUpdated: 1587131351,
      data: [{
        name: 'name',
        items: [],
      }],
    }];

    component.detailData = detailData;
    expect(component.data.length).toEqual(1);

    const noData = [{
      title: 'buildTitle',
      url: 'buildUrl',
      lastUpdated: 1587131351,
    }];

    component.detailData = noData;
    expect(component.data[0]).toEqual(noData);
  });
});
