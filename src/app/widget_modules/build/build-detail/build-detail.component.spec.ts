import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BuildDetailComponent } from './build-detail.component';
import {NgbActiveModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {MatStepperModule} from '@angular/material/stepper';
import {MatIconModule} from '@angular/material/icon';
import {MatTooltipModule} from '@angular/material/tooltip';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';

describe('BuildDetailComponent', () => {
  let component: BuildDetailComponent;
  let fixture: ComponentFixture<BuildDetailComponent>;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BuildDetailComponent ],
      providers: [ NgbActiveModal ],
      schemas: [ CUSTOM_ELEMENTS_SCHEMA ],
      imports: [NgbModule, MatStepperModule, MatIconModule, MatTooltipModule, BrowserAnimationsModule, RouterTestingModule.withRoutes([])]
    })
    .compileComponents();
  }));

  afterEach(() => {
    fixture.destroy();
  });

  beforeEach( async () => {
    fixture = TestBed.createComponent(BuildDetailComponent);
    component = fixture.componentInstance;
    const detailData = [{
      title: 'buildTitle',
      url: 'buildUrl',
      lastUpdated: 1587131351,
      subtitles: [111],
      stages: [
        {
          stageId: 111,
          name: 'Test Stage',
          status: 'SUCCESS',
          startTimeMillis: 1111,
          durationMillis: 1000,
          _links: {
            self: {
              href: 'url-string'
            }
          }
        }
      ],
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
      subtitles: [111],
      stages: [
        {
          stageId: 111,
          name: 'Test Stage',
          status: 'SUCCESS',
          startTimeMillis: 1111,
          durationMillis: 1000,
          _links: {
            self: {
              href: 'test_href'
            }
          }
        }
      ],
    }];
    component.data = detailData;
    expect(component.data.length).toEqual(1);
  });

  it('should format time currectly if duration is available', () => {
    const detailData = [{
      title: 'buildTitle',
      url: 'buildUrl',
      lastUpdated: 1587131351,
      subtitles: [111],
      duration: 12345,
      stages: [
        {
          stageId: 111,
          name: 'Test Stage',
          status: 'SUCCESS',
          startTimeMillis: 1111,
          durationMillis: 1000,
          _links: {
            self: {
              href: 'test_href'
            }
          }
        }
      ],
    }];
    fixture = TestBed.createComponent(BuildDetailComponent);
    component = fixture.componentInstance;
    component.data = detailData;
    fixture.detectChanges();
    component.readableDuration = component.convertToReadable(component.data[0].duration);
    expect(component.readableDuration).toBe('00:00:12');
  });
});
