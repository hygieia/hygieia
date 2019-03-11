import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { WidgetComponent } from './widget/widget.component';
import { TwoByTwoLayoutComponent } from './two-by-two-layout/two-by-two-layout.component';
import { LayoutComponent } from './layout/layout.component';
import { ChartDirective } from './chart.directive';
import { ChartComponent } from './chart/chart.component';
import { LayoutDirective } from './layout.directive';
import { LineChartComponent } from './line-chart/line-chart.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';

@NgModule({
    declarations: [WidgetComponent, TwoByTwoLayoutComponent, LayoutComponent, ChartDirective,
        ChartComponent, LayoutDirective, LineChartComponent],
    entryComponents: [TwoByTwoLayoutComponent, LineChartComponent],
    imports: [
        CommonModule,
        ReactiveFormsModule,
        NgxChartsModule
    ],
    exports: [
        ReactiveFormsModule,
        CommonModule,
        TwoByTwoLayoutComponent,
        LayoutComponent,
        WidgetComponent,
        LineChartComponent,
        ChartComponent,
        LayoutDirective,
        ChartDirective
    ]
})
export class SharedModule { }
