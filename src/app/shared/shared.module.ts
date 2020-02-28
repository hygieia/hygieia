import { DragDropModule } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { NgxUIModule } from '@swimlane/ngx-ui';
import { TimeAgoPipe } from 'time-ago-pipe';

import { CaponeTemplateComponent } from '../screen_modules/team-dashboard/capone-template/capone-template.component';
import { BuildConfigFormComponent } from '../widget_modules/build/build-config-form/build-config-form.component';
import { BuildDetailComponent } from '../widget_modules/build/build-detail/build-detail.component';
import { BuildWidgetComponent } from '../widget_modules/build/build-widget/build-widget.component';
import { ChartDirective } from './charts/chart.directive';
import { ChartComponent } from './charts/chart/chart.component';
import { ClickListComponent } from './charts/click-list/click-list.component';
import { ComboChartComponent } from './charts/combo-chart/combo-chart.component';
import { ComboSeriesVerticalComponent } from './charts/combo-series-vertical/combo-series-vertical.component';
import { GaugeChartComponent } from './charts/gauge-chart/gauge-chart.component';
import { LineAndBarChartComponent } from './charts/line-and-bar-chart/line-and-bar-chart.component';
import { LineChartComponent } from './charts/line-chart/line-chart.component';
import { NumberCardChartComponent } from './charts/number-card-chart/number-card-chart.component';
import { PlainTextChartComponent } from './charts/plain-text-chart/plain-text-chart.component';
import { DashStatusComponent } from './dash-status/dash-status.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { LayoutDirective } from './layouts/layout.directive';
import { LayoutComponent } from './layouts/layout/layout.component';
import { TwoByTwoLayoutComponent } from './layouts/two-by-two-layout/two-by-two-layout.component';
import { ConfirmationModalComponent } from './modals/confirmation-modal/confirmation-modal.component';
import { DetailModalComponent } from './modals/detail-modal/detail-modal.component';
import { DetailModalDirective } from './modals/detail-modal/detail-modal.directive';
import { FormModalComponent } from './modals/form-modal/form-modal.component';
import { FormModalDirective } from './modals/form-modal/form-modal.directive';
import { PaginationComponent } from './pagination/pagination.component';
import { MinutesPipe } from './pipes/minutes.pipe';
import { BaseTemplateComponent } from './templates/base-template/base-template.component';
import { TemplatesDirective } from './templates/templates.directive';
import { WidgetHeaderComponent } from './widget-header/widget-header.component';
import { PlaceholderWidgetComponent } from './widget/placeholder-widget/placeholder-widget.component';
import { WidgetComponent } from './widget/widget.component';
import { WidgetDirective } from './widget/widget.directive';
import { DeployWidgetComponent } from '../widget_modules/deploy/deploy-widget/deploy-widget.component';
import { DeployDetailComponent } from '../widget_modules/deploy/deploy-detail/deploy-detail.component';
import { OneChartLayoutComponent } from './layouts/one-chart-layout/one-chart-layout.component';
import { DeployConfigFormComponent } from '../widget_modules/deploy/deploy-config-form/deploy-config-form.component';

@NgModule({
  declarations: [
    DeployConfigFormComponent,
    BaseTemplateComponent,
    BuildConfigFormComponent,
    BuildDetailComponent,
    BuildWidgetComponent,
    CaponeTemplateComponent,
    ChartComponent,
    ChartDirective,
    ClickListComponent,
    ComboChartComponent,
    ComboSeriesVerticalComponent,
    ConfirmationModalComponent,
    DashboardComponent,
    DetailModalComponent,
    DetailModalDirective,
    FormModalComponent,
    FormModalDirective,
    LayoutComponent,
    LayoutDirective,
    LineAndBarChartComponent,
    LineChartComponent,
    MinutesPipe,
    NumberCardChartComponent,
    PaginationComponent,
    PlaceholderWidgetComponent,
    TemplatesDirective,
    TimeAgoPipe,
    TwoByTwoLayoutComponent,
    WidgetComponent,
    WidgetDirective,
    WidgetHeaderComponent,
    DashStatusComponent,
    PlainTextChartComponent,
    DeployDetailComponent,
    DeployWidgetComponent,
    OneChartLayoutComponent,
    GaugeChartComponent,
    DeployConfigFormComponent
  ],
  entryComponents: [
    DeployDetailComponent,
    DeployWidgetComponent,
    BuildConfigFormComponent,
    BuildDetailComponent,
    BuildWidgetComponent,
    CaponeTemplateComponent,
    ClickListComponent,
    ComboChartComponent,
    ConfirmationModalComponent,
    DetailModalComponent,
    FormModalComponent,
    LineAndBarChartComponent,
    LineChartComponent,
    NumberCardChartComponent,
    PlaceholderWidgetComponent,
    PlainTextChartComponent,
    TwoByTwoLayoutComponent,
    OneChartLayoutComponent,
    GaugeChartComponent,
    DeployConfigFormComponent
  ],
  imports: [
    CommonModule,
    DragDropModule,
    FlexLayoutModule,
    NgbModule,
    NgxChartsModule,
    NgxUIModule,
    ReactiveFormsModule
  ],
  exports: [
    BuildWidgetComponent,
    CaponeTemplateComponent,
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
    TemplatesDirective,
    TwoByTwoLayoutComponent,
    WidgetComponent,
    WidgetDirective,
    WidgetHeaderComponent,
    GaugeChartComponent
  ]
})
export class SharedModule { }
