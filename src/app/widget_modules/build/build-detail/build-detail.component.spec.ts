import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BuildDetailComponent } from './build-detail.component';
import {NgbActiveModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {MatStepperModule} from '@angular/material/stepper';
import {MatIconModule} from '@angular/material/icon';
import {MatTooltipModule} from '@angular/material/tooltip';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';




describe('BuildDetailComponent', () => {
  let component: BuildDetailComponent;
  let fixture: ComponentFixture<BuildDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BuildDetailComponent ],
      providers: [ NgbActiveModal ],
      schemas: [ CUSTOM_ELEMENTS_SCHEMA ],
      imports: [NgbModule, MatStepperModule, MatIconModule, MatTooltipModule, BrowserAnimationsModule]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BuildDetailComponent);
    component = fixture.componentInstance;
    const detailData = [{
      title: 'buildTitle',
      url: 'buildUrl',
      lastUpdated: 1587131351,
      data: [{
        name: 'name',
        items: [],
      }],
      stages: [],

    }];
    component.data = detailData;
    fixture.detectChanges();
  });

  it('should create', () => {
    fixture = TestBed.createComponent(BuildDetailComponent);

    component = fixture.componentInstance;
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
      stages: []
    }];

    component.data = detailData;
    expect(component.data.length).toEqual(1);

    const noData = [{
      title: 'buildTitle',
      url: 'buildUrl',
      lastUpdated: 1587131351,
      stages: [

      ]
    }];

    component.detailData = [noData];
    expect(component.data[0]).toEqual(noData);
  });
});
