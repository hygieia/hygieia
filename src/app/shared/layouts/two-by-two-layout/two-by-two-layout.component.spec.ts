import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, ViewChild, ElementRef, NgModule } from '@angular/core';
import { TwoByTwoLayoutComponent } from './two-by-two-layout.component';
import { LineChartComponent } from '../../charts/line-chart/line-chart.component';
import { By } from '@angular/platform-browser';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SharedModule } from '../../shared.module';
import { LayoutComponent } from '../layout/layout.component';
import { ChartComponent } from '../../charts/chart/chart.component';
import { ChartDirective } from '../../charts/chart.directive';

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
    let fixture: ComponentFixture<TwoByTwoLayoutComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [LayoutComponent, TwoByTwoLayoutComponent, ChartDirective],
            imports: [TestModule, NgxChartsModule, BrowserAnimationsModule],
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(TwoByTwoLayoutComponent);
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
        });
        fixture.detectChanges();
        expect(fixture.componentInstance.chartContainers).toBeDefined();
        const childDebugElement = fixture.debugElement.query(By.directive(LineChartComponent));
        expect(childDebugElement).toBeTruthy();
    });
});
