import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TwoByTwoLayoutComponent } from './two-by-two-layout.component';
import { LineChartComponent } from '../../charts/line-chart/line-chart.component';
import { By } from '@angular/platform-browser';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SharedModule } from '../../shared.module';


describe('TwoByTwoLayoutComponent', () => {
    let component: TwoByTwoLayoutComponent;
    let fixture: ComponentFixture<TwoByTwoLayoutComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [],
            imports: [SharedModule, NgxChartsModule, BrowserAnimationsModule],
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
        const childDebugElement = fixture.debugElement.query(By.directive(LineChartComponent));
        expect(childDebugElement).toBeTruthy();
    });
});
