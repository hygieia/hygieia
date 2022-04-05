import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgxChartsModule } from '@swimlane/ngx-charts';

import { GaugeChartComponent } from './gauge-chart.component';

describe('GaugeChartComponent', () => {
  let component: GaugeChartComponent;
  let fixture: ComponentFixture<GaugeChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [NgxChartsModule, BrowserAnimationsModule],
      declarations: [ GaugeChartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GaugeChartComponent);
    component = fixture.componentInstance;
    component.colorScheme = 'vivid';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should format append percent', () => {
    expect(component.formatAppendPercent(50)).toEqual('50%');
    expect(component.formatting(75)).toEqual('75%');
  });
});
