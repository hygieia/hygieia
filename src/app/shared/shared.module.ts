import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { WidgetComponent } from './widget/widget.component';
import { TwoByTwoLayoutComponent } from './layouts/two-by-two-layout/two-by-two-layout.component';
import { LayoutComponent } from './layouts/layout/layout.component';
import { ChartDirective } from './charts/chart.directive';
import { ChartComponent } from './charts/chart/chart.component';
import { LayoutDirective } from './layouts/layout.directive';
import { LineChartComponent } from './charts/line-chart/line-chart.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { ModalComponent } from "./modal/modal.component";
import { ConfigureModalComponent} from "./configure-modal/configure-modal.component";

@NgModule({
    declarations: [
      ConfigureModalComponent,
      ModalComponent,
      WidgetComponent,
      TwoByTwoLayoutComponent,
      LayoutComponent,
      ChartDirective,
      ChartComponent,
      LayoutDirective,
      LineChartComponent
    ],
    entryComponents: [
      TwoByTwoLayoutComponent,
      LineChartComponent,
      ModalComponent
    ],
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
