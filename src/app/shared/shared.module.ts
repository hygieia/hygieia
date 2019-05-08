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
import { BuildWidgetComponent } from '../widget_modules/build/build-widget/build-widget.component';
import { ChartDirective } from './charts/chart.directive';
import { ChartComponent } from './charts/chart/chart.component';
import { ClickListComponent } from './charts/click-list/click-list.component';
import { ComboChartComponent } from './charts/combo-chart/combo-chart.component';
import { ComboSeriesVerticalComponent } from './charts/combo-series-vertical/combo-series-vertical.component';
import { LineAndBarChartComponent } from './charts/line-and-bar-chart/line-and-bar-chart.component';
import { LineChartComponent } from './charts/line-chart/line-chart.component';
import { NumberCardChartComponent } from './charts/number-card-chart/number-card-chart.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { LayoutDirective } from './layouts/layout.directive';
import { LayoutComponent } from './layouts/layout/layout.component';
import { TwoByTwoLayoutComponent } from './layouts/two-by-two-layout/two-by-two-layout.component';
import { PaginationComponent } from './pagination/pagination.component';
import { BaseTemplateComponent } from './templates/base-template/base-template.component';
import { TemplatesDirective } from './templates/templates.directive';
import { PlaceholderWidgetComponent } from './widget/placeholder-widget/placeholder-widget.component';
import { WidgetComponent } from './widget/widget.component';
import { WidgetDirective } from './widget/widget.directive';

@NgModule({
  declarations: [
    BaseTemplateComponent,
    BuildConfigFormComponent,
    BuildWidgetComponent,
    CaponeTemplateComponent,
    ChartComponent,
    ChartDirective,
    ClickListComponent,
    ComboChartComponent,
    ComboSeriesVerticalComponent,
    DashboardComponent,
    LayoutComponent,
    LayoutDirective,
    LineAndBarChartComponent,
    LineChartComponent,
    NumberCardChartComponent,
    PaginationComponent,
    PlaceholderWidgetComponent,
    TemplatesDirective,
    TimeAgoPipe,
    TwoByTwoLayoutComponent,
    WidgetDirective,
    WidgetComponent
  ],
  entryComponents: [
    BuildConfigFormComponent,
    BuildWidgetComponent,
    CaponeTemplateComponent,
    ClickListComponent,
    ComboChartComponent,
    LineAndBarChartComponent,
    LineChartComponent,
    NumberCardChartComponent,
    PlaceholderWidgetComponent,
    TwoByTwoLayoutComponent
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
    WidgetComponent
  ]
})
export class SharedModule { }
