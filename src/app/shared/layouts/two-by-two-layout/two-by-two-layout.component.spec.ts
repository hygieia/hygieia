import { NgModule } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgxChartsModule } from '@swimlane/ngx-charts';

import { ChartDirective } from '../../charts/chart.directive';
import { ChartComponent } from '../../charts/chart/chart.component';
import { LineChartComponent } from '../../charts/line-chart/line-chart.component';
import { LayoutComponent } from '../layout/layout.component';
import { TwoByTwoLayoutComponent } from './two-by-two-layout.component';

// Work around for dynamic component loading testing
@NgModule({
  declarations: [ChartComponent, LineChartComponent],
  imports: [NgxChartsModule, BrowserAnimationsModule],
  entryComponents: [
    LineChartComponent
  ]
})
class TestModule { }

describe('TwoByTwoLayoutComponent', () => {
  let component: TwoByTwoLayoutComponent;
  let componentBase: LayoutComponent;
  let fixture: ComponentFixture<TwoByTwoLayoutComponent>;
  let fixtureBase: ComponentFixture<LayoutComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [LayoutComponent, TwoByTwoLayoutComponent, ChartDirective],
      imports: [TestModule, NgxChartsModule, BrowserAnimationsModule],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixtureBase = TestBed.createComponent(LayoutComponent);
    fixture = TestBed.createComponent(TwoByTwoLayoutComponent);
    componentBase = fixtureBase.componentInstance;
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create chart components', () => {
    component.charts = [];
    component.charts.push({
      component: LineChartComponent,
      data: {},
      xAxisLabel: 'Test',
      yAxisLabel: 'Test',
      colorScheme: 'vivid',
      title: 'Test'
    });
    fixture.detectChanges();
    expect(fixture.componentInstance.chartContainers).toBeDefined();
    const childDebugElement = fixture.debugElement.query(By.directive(LineChartComponent));
    expect(childDebugElement).toBeTruthy();
  });

  it('should load components', () => {
    component.charts = [];
    component.charts.push({
      component: LineChartComponent,
      data: {},
      xAxisLabel: 'Test',
      yAxisLabel: 'Test',
      colorScheme: 'vivid',
      title: 'Test'
    });
    fixture.detectChanges();
    expect(fixture.componentInstance.childChartTags).toBeDefined();
    expect(fixture.componentInstance.chartContainers).toBeDefined();
    component.ngAfterViewInit();
    expect(component.chartComponents).toBeDefined();

  });
});
