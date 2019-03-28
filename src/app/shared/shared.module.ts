import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { FormControl } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { WidgetComponent } from './widget/widget.component';
import { TwoByTwoLayoutComponent } from './layouts/two-by-two-layout/two-by-two-layout.component';
import { LayoutComponent } from './layouts/layout/layout.component';
import { ChartDirective } from './charts/chart.directive';
import { ChartComponent } from './charts/chart/chart.component';
import { LayoutDirective } from './layouts/layout.directive';
import { LineChartComponent } from './charts/line-chart/line-chart.component';
import { PaginationComponent } from './pagination/pagination.component';

@NgModule({
    declarations: [WidgetComponent, TwoByTwoLayoutComponent, LayoutComponent, ChartDirective,
        ChartComponent, LayoutDirective, LineChartComponent, PaginationComponent],
    entryComponents: [TwoByTwoLayoutComponent, LineChartComponent],
    imports: [
        CommonModule,
        ReactiveFormsModule,
        NgxChartsModule,
        NgbModule
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
        ChartDirective,
        PaginationComponent
    ]
})
export class SharedModule { }
