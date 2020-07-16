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
import { ChartDirective } from './charts/chart.directive';
import { ChartComponent } from './charts/chart/chart.component';
import { ClickListComponent } from './charts/click-list/click-list.component';
import { ComboChartComponent } from './charts/combo-chart/combo-chart.component';
import { ComboSeriesVerticalComponent } from './charts/combo-series-vertical/combo-series-vertical.component';
import { GaugeChartComponent } from './charts/gauge-chart/gauge-chart.component';
import { LineAndBarChartComponent } from './ngx-charts/line-and-bar-chart/line-and-bar-chart.component';
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
import { OneChartLayoutComponent } from './layouts/one-chart-layout/one-chart-layout.component';
import { OneByTwoLayoutComponent } from './layouts/one-by-two-layout/one-by-two-layout.component';
import { HorizontalBarChartComponent } from './charts/horizontal-bar-chart/horizontal-bar-chart.component';
import {BarHorizontalComponent} from './ngx-charts/bar-horizontal/bar-horizontal.component';
import {PieGridChartComponent} from './charts/pie-grid-chart/pie-grid-chart.component';
import {PieGridComponent} from './ngx-charts/pie-grid/pie-grid.component';
import { AuditModalComponent } from './modals/audit-modal/audit-modal.component';
import { TwoByOneLayoutComponent } from './layouts/two-by-one-layout/two-by-one-layout.component';
import {DeleteConfirmModalComponent} from './modals/delete-confirm-modal/delete-confirm-modal.component';
import {TabsModule} from './ngx-ui/tabs/tabs.module';
// tslint:disable-next-line:max-line-length
import {OneByTwoLayoutTableChartComponent} from './layouts/one-by-two-layout-table-chart/one-by-two-layout-table-chart.component';
import { NavbarComponent } from '../core/navbar/navbar.component';
import {RouterModule} from '@angular/router';
import {DeleteConfirmModalDirective} from './modals/delete-confirm-modal/delete-confirm-modal.directive';
import {RotationChartComponent} from './charts/rotation/rotation-chart.component';
import {NbActionsModule, NbCardModule, NbSearchModule, NbTabsetModule, NbUserModule} from '@nebular/theme';
import {ConfirmationModalDirective} from './modals/confirmation-modal/confirmation-modal.directive';
import {DashEditComponent} from './dash-edit/dash-edit.component';
import {DashTrashComponent} from './dash-trash/dash-trash.component';
import {AdminDeleteComponent} from './modals/admin-delete/admin-delete.component';
import {EditDashboardModalComponent} from './modals/edit-dashboard-modal/edit-dashboard-modal.component';
import {AdminOrderByPipe} from './pipes/order-by.pipe';
import {AdminFilterPipe} from './pipes/filter.pipe';
import {UserDataService} from '../admin_modules/admin_dashboard/services/user-data.service';
import {AdminDashboardService} from '../admin_modules/admin_dashboard/services/dashboard.service';
import {DashboardDataService} from '../admin_modules/admin_dashboard/services/dashboard-data.service';
import {TabsLabeltemplateFixtureComponent} from './ngx-ui/tabs/fixtures/tabs-label-template.fixture';
import {TabsMultipleActiveFixtureComponent} from './ngx-ui/tabs/fixtures/tabs-multiple-active.fixture';
import {TabsFixtureComponent} from './ngx-ui/tabs/fixtures/tabs.fixture';

@NgModule({
  declarations: [
    EditDashboardModalComponent,
    AdminDeleteComponent,
    DashEditComponent,
    DashTrashComponent,
    BarHorizontalComponent,
    BaseTemplateComponent,
    CaponeTemplateComponent,
    ChartComponent,
    ChartDirective,
    ClickListComponent,
    ComboChartComponent,
    ComboSeriesVerticalComponent,
    ConfirmationModalComponent,
    DashboardComponent,
    DeleteConfirmModalComponent,
    DeleteConfirmModalDirective,
    DetailModalComponent,
    DetailModalDirective,
    FormModalComponent,
    FormModalDirective,
    HorizontalBarChartComponent,
    LayoutComponent,
    LayoutDirective,
    LineAndBarChartComponent,
    LineChartComponent,
    MinutesPipe,
    NumberCardChartComponent,
    OneByTwoLayoutComponent,
    OneByTwoLayoutTableChartComponent,
    PaginationComponent,
    PieGridComponent,
    PieGridChartComponent,
    PlaceholderWidgetComponent,
    RotationChartComponent,
    TemplatesDirective,
    TimeAgoPipe,
    TwoByTwoLayoutComponent,
    WidgetComponent,
    WidgetDirective,
    WidgetHeaderComponent,
    DashStatusComponent,
    PlainTextChartComponent,
    TwoByOneLayoutComponent,
    OneChartLayoutComponent,
    GaugeChartComponent,
    AuditModalComponent,
    TwoByOneLayoutComponent,
    NavbarComponent,
    ConfirmationModalDirective,
    AdminDeleteComponent,
    AdminFilterPipe,
    AdminOrderByPipe,
    TabsLabeltemplateFixtureComponent,
    TabsMultipleActiveFixtureComponent,
    TabsFixtureComponent
  ],
  entryComponents: [
    EditDashboardModalComponent,
    AdminDeleteComponent,
    DeleteConfirmModalComponent,
    BarHorizontalComponent,
    CaponeTemplateComponent,
    ClickListComponent,
    ComboChartComponent,
    ConfirmationModalComponent,
    DeleteConfirmModalComponent,
    DetailModalComponent,
    FormModalComponent,
    GaugeChartComponent,
    HorizontalBarChartComponent,
    LineAndBarChartComponent,
    LineChartComponent,
    NumberCardChartComponent,
    OneByTwoLayoutComponent,
    OneByTwoLayoutTableChartComponent,
    OneChartLayoutComponent,
    PieGridComponent,
    PieGridChartComponent,
    PlaceholderWidgetComponent,
    PlainTextChartComponent,
    TwoByOneLayoutComponent,
    RotationChartComponent,
    TwoByTwoLayoutComponent,
    TwoByTwoLayoutComponent,
    AuditModalComponent
  ],
  imports: [
    CommonModule,
    DragDropModule,
    FlexLayoutModule,
    NgbModule,
    NgxChartsModule,
    NgxUIModule,
    ReactiveFormsModule,
    TabsModule,
    RouterModule,
    NbActionsModule,
    NbUserModule,
    NbSearchModule,
    NbCardModule,
    NbTabsetModule,
  ],
  exports: [
    DashEditComponent,
    DashTrashComponent,
    AdminFilterPipe,
    AdminOrderByPipe,
    BarHorizontalComponent,
    CaponeTemplateComponent,
    ChartComponent,
    ChartDirective,
    ComboChartComponent,
    ComboSeriesVerticalComponent,
    CommonModule,
    HorizontalBarChartComponent,
    LayoutComponent,
    LayoutDirective,
    LineAndBarChartComponent,
    LineChartComponent,
    NumberCardChartComponent,
    OneByTwoLayoutComponent,
    OneByTwoLayoutTableChartComponent,
    PaginationComponent,
    PieGridComponent,
    PieGridChartComponent,
    ReactiveFormsModule,
    RotationChartComponent,
    TemplatesDirective,
    TwoByTwoLayoutComponent,
    TwoByOneLayoutComponent,
    WidgetComponent,
    WidgetDirective,
    WidgetHeaderComponent,
    GaugeChartComponent,
    NavbarComponent,
    AdminDeleteComponent,
    EditDashboardModalComponent
  ],
  providers: [
    UserDataService,
    AdminDashboardService,
    DashboardDataService
  ]
})
export class SharedModule { }
