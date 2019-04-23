import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxUIModule } from '@swimlane/ngx-ui';
import { TimeAgoPipe } from 'time-ago-pipe';

import { ChartDirective } from './charts/chart.directive';
import { ChartComponent } from './charts/chart/chart.component';
import { ClickListComponent } from './charts/click-list/click-list.component';
import { ComboChartComponent } from './charts/combo-chart/combo-chart.component';
import { ComboSeriesVerticalComponent } from './charts/combo-series-vertical/combo-series-vertical.component';
import { LineAndBarChartComponent } from './charts/line-and-bar-chart/line-and-bar-chart.component';
import { LineChartComponent } from './charts/line-chart/line-chart.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { FormModalComponent} from './modals/form-modal/form-modal.component';
import { DetailModalComponent } from './modals/detail-modal/detail-modal.component';
import { ConfirmationModalComponent } from './modals/confirmation-modal/confirmation-modal.component';
import { ModalDirective } from './modals/modal.directive';
import { TestFormComponent} from '../widget_modules/build/test-form/test-form.component';
import { NumberCardChartComponent } from './charts/number-card-chart/number-card-chart.component';
import { LayoutDirective } from './layouts/layout.directive';
import { LayoutComponent } from './layouts/layout/layout.component';
import { TwoByTwoLayoutComponent } from './layouts/two-by-two-layout/two-by-two-layout.component';
import { PaginationComponent } from './pagination/pagination.component';
import { WidgetComponent } from './widget/widget.component';
import { WidgetHeaderComponent } from './widget-header/widget-header.component';
import {WidgetDirective} from './widget/widget.directive';
import {BuildWidgetComponent} from '../widget_modules/build/build-widget/build-widget.component';

@NgModule({
  declarations: [
    ChartComponent,
    ChartDirective,
    ClickListComponent,
    ComboChartComponent,
    ComboSeriesVerticalComponent,
    ConfirmationModalComponent,
    DetailModalComponent,
    FormModalComponent,
    LayoutComponent,
    LayoutDirective,
    LineAndBarChartComponent,
    LineChartComponent,
    ModalDirective,
    NumberCardChartComponent,
    PaginationComponent,
    TestFormComponent,
    TimeAgoPipe,
    TwoByTwoLayoutComponent,
    WidgetComponent,
    WidgetHeaderComponent,
    WidgetDirective
  ],
  entryComponents: [
    ClickListComponent,
    ComboChartComponent,
    ConfirmationModalComponent,
    DetailModalComponent,
    FormModalComponent,
    LineAndBarChartComponent,
    LineChartComponent,
    NumberCardChartComponent,
    TestFormComponent,
    TwoByTwoLayoutComponent
  ],
  imports: [
    CommonModule,
    NgbModule,
    NgxChartsModule,
    NgxUIModule,
    ReactiveFormsModule
  ],
  exports: [
    ChartComponent,
    ChartDirective,
    ComboChartComponent,
    ComboSeriesVerticalComponent,
    CommonModule,
    LayoutComponent,
    LayoutDirective,
    LineAndBarChartComponent,
    LineChartComponent,
    NumberCardChartComponent,
    PaginationComponent,
    ReactiveFormsModule,
    TwoByTwoLayoutComponent,
    WidgetComponent,
    WidgetHeaderComponent,
    WidgetDirective
  ]
})
export class SharedModule { }
