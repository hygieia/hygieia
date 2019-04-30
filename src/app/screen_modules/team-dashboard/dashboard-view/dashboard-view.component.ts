import { AfterViewInit, ChangeDetectorRef, Component, ComponentFactoryResolver, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DashboardService } from 'src/app/shared/dashboard.service';
import { DashboardComponent } from 'src/app/shared/dashboard/dashboard.component';
import { TemplatesDirective } from 'src/app/shared/templates/templates.directive';
import { PlaceholderWidgetComponent } from 'src/app/shared/widget/placeholder-widget/placeholder-widget.component';
import { BuildWidgetComponent } from 'src/app/widget_modules/build/build-widget/build-widget.component';

import { CaponeTemplateComponent } from '../capone-template/capone-template.component';
import { ITemplate } from './dashboard-view';

@Component({
  selector: 'app-dashboard-view',
  templateUrl: './dashboard-view.component.html',
  styleUrls: ['./dashboard-view.component.scss']
})
export class DashboardViewComponent extends DashboardComponent implements OnInit, AfterViewInit {

  teamDashboard: ITemplate;
  dashboardId: string;
  @ViewChild(TemplatesDirective) childTemplateTag: TemplatesDirective;

  constructor(componentFactoryResolver: ComponentFactoryResolver,
              cdr: ChangeDetectorRef,
              private route: ActivatedRoute,
              private dashboardService: DashboardService) {
    super(componentFactoryResolver, cdr);
  }

  ngOnInit() {
    this.dashboardService.clearDashboard();
    console.log(this.route.snapshot);
    this.dashboardId = this.route.snapshot.paramMap.get('id');
    console.log(this.dashboardId);
    // this.dashboardService.loadDashboard('596acb685797b408c8f51e8d');
    this.dashboardService.loadDashboard(this.dashboardId);

    // get template type for dashboard and create template component
    // noinspection TypeScriptValidateTypes
    // noinspection TypeScriptValidateTypes
   // let templateName = this.temaDashboard.template;
    this.baseTemplate = CaponeTemplateComponent;

    // noinspection TypeScriptValidateTypes
    this.widgets = [
      {
        component: PlaceholderWidgetComponent,
        status: 'Success'
      },
      {
        component: BuildWidgetComponent,
        status: 'Success'
      }
      ,
      {
        component: PlaceholderWidgetComponent,
        status: 'Success'
      },
      {
        component: PlaceholderWidgetComponent,
        status: 'Success'
      },
      {
        component: PlaceholderWidgetComponent,
        status: 'Success'
      },
      {
        component: PlaceholderWidgetComponent,
        status: 'Success'
      }
    ];
  }

  ngAfterViewInit() {
    super.loadComponent(this.childTemplateTag);
  }

}


