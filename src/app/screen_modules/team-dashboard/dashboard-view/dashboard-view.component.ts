import { AfterViewInit, ChangeDetectorRef, Component, ComponentFactoryResolver, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DashboardService } from 'src/app/shared/dashboard.service';
import { DashboardComponent } from 'src/app/shared/dashboard/dashboard.component';
import { TemplatesDirective } from 'src/app/shared/templates/templates.directive';
import { PlaceholderWidgetComponent } from 'src/app/shared/widget/placeholder-widget/placeholder-widget.component';
import { BuildWidgetComponent } from 'src/app/widget_modules/build/build-widget/build-widget.component';

import { CaponeTemplateComponent } from '../capone-template/capone-template.component';
import { ITemplate } from './dashboard-view';
import {Placeholder} from '@angular/compiler/src/i18n/i18n_ast';
import {BuildConfigFormComponent} from '../../../widget_modules/build/build-config-form/build-config-form.component';

@Component({
  selector: 'app-dashboard-view',
  templateUrl: './dashboard-view.component.html',
  styleUrls: ['./dashboard-view.component.scss']
})
export class DashboardViewComponent extends DashboardComponent implements OnInit, AfterViewInit {

  teamDashboard: ITemplate;
  dashboardId: string;
  @ViewChild(TemplatesDirective , {static: false}) childTemplateTag: TemplatesDirective;

  constructor(componentFactoryResolver: ComponentFactoryResolver,
              cdr: ChangeDetectorRef,
              private route: ActivatedRoute,
              private dashboardService: DashboardService) {
    super(componentFactoryResolver, cdr);
  }

  ngOnInit() {
    this.dashboardService.clearDashboard();
    this.dashboardId = this.route.snapshot.paramMap.get('id');
    this.dashboardService.loadDashboard(this.dashboardId);

    this.baseTemplate = CaponeTemplateComponent;

    this.widgets = [
      {
        title: 'Placeholder',
        component: PlaceholderWidgetComponent,
        status: 'Success',
        widgetSize: 'col-xl-3',
        configForm: BuildConfigFormComponent
      },
      {
        title: 'Build',
        component: BuildWidgetComponent,
        status: 'Success',
        widgetSize: 'col-xl-6',
        configForm: BuildConfigFormComponent
      }
      ,
      {
        title: 'Placeholder',
        component: PlaceholderWidgetComponent,
        status: 'Success',
        widgetSize: 'col-xl-3',
        configForm: BuildConfigFormComponent
      },
      {
        title: 'Placeholder',
        component: PlaceholderWidgetComponent,
        status: 'Success',
        widgetSize: 'col-xl-4',
        configForm: BuildConfigFormComponent
      },
      {
        title: 'Placeholder',
        component: PlaceholderWidgetComponent,
        status: 'Success',
        widgetSize: 'col-xl-4',
        configForm: BuildConfigFormComponent
      },
      {
        title: 'Placeholder',
        component: PlaceholderWidgetComponent,
        status: 'Success',
        widgetSize: 'col-xl-4',
        configForm: BuildConfigFormComponent
      }
    ];
  }

  ngAfterViewInit() {
    super.loadComponent(this.childTemplateTag);
  }

}


