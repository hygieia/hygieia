import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { NgxUIModule } from '@swimlane/ngx-ui';
import { TimeAgoPipe } from 'time-ago-pipe';

import { ChartDirective } from './charts/chart.directive';
import { ChartComponent } from './charts/chart/chart.component';
import { ClickListComponent } from './charts/click-list/click-list.component';
import { ComboChartComponent } from './charts/combo-chart/combo-chart.component';
import { ComboSeriesVerticalComponent } from './charts/combo-series-vertical/combo-series-vertical.component';
import { LineAndBarChartComponent } from './charts/line-and-bar-chart/line-and-bar-chart.component';
import { LineChartComponent } from './charts/line-chart/line-chart.component';
import { NumberCardChartComponent } from './charts/number-card-chart/number-card-chart.component';
import { LayoutDirective } from './layouts/layout.directive';
import { LayoutComponent } from './layouts/layout/layout.component';
import { TwoByTwoLayoutComponent } from './layouts/two-by-two-layout/two-by-two-layout.component';
import { PaginationComponent } from './pagination/pagination.component';
import { WidgetComponent } from './widget/widget.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { TemplatesDirective } from './templates/templates.directive';
import { WidgetDirective } from './widget/widget.directive';
import { BaseTemplateComponent } from './templates/base-template/base-template.component';
import {BuildWidgetComponent} from "../widget_modules/build/build-widget/build-widget.component";
import {FlexLayoutModule} from "@angular/flex-layout";
import {DragDropModule} from "@angular/cdk/drag-drop";
import {CaponeTemplateComponent} from "../screen_modules/team-dashboard/capone-template/capone-template.component";

@NgModule({
  declarations: [WidgetComponent, TwoByTwoLayoutComponent, LayoutComponent, ChartDirective,
    ChartComponent, LayoutDirective, LineChartComponent, NumberCardChartComponent,
    LineAndBarChartComponent, ComboChartComponent, ComboSeriesVerticalComponent, ClickListComponent,
    TimeAgoPipe, PaginationComponent, DashboardComponent, TemplatesDirective, WidgetDirective, BaseTemplateComponent,CaponeTemplateComponent,BuildWidgetComponent],
  entryComponents: [TwoByTwoLayoutComponent, LineChartComponent, NumberCardChartComponent,
    LineAndBarChartComponent, ComboChartComponent, ClickListComponent,CaponeTemplateComponent,BuildWidgetComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    NgxChartsModule,
    NgxUIModule,
    NgbModule,
    FlexLayoutModule,
    DragDropModule
  ],
  exports: [
    ReactiveFormsModule,
    CommonModule,
    TwoByTwoLayoutComponent,
    LayoutComponent,
    WidgetComponent,
    LineChartComponent,
    NumberCardChartComponent,
    ComboChartComponent,
    LineAndBarChartComponent,
    ComboSeriesVerticalComponent,
    ChartComponent,
    LayoutDirective,
    ChartDirective,
    PaginationComponent,
    TemplatesDirective,
    CaponeTemplateComponent,
    BuildWidgetComponent

  ]
})
export class SharedModule { }
