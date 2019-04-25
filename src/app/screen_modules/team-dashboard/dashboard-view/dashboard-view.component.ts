import { Component, OnInit, AfterViewInit, ComponentFactoryResolver, ChangeDetectorRef, ViewChild } from '@angular/core';
import { DashboardComponent } from 'src/app/shared/dashboard/dashboard.component';
import { TemplatesDirective } from 'src/app/shared/templates/templates.directive';
import { CaponeTemplateComponent } from '../capone-template/capone-template.component';
import { BuildWidgetComponent } from 'src/app/widget_modules/build/build-widget/build-widget.component';
import {ITemplate} from './dashboard-view';

@Component({
  selector: 'app-dashboard-view',
  templateUrl: './dashboard-view.component.html',
  styleUrls: ['./dashboard-view.component.scss']
})
export class DashboardViewComponent extends DashboardComponent implements OnInit, AfterViewInit {

  teamDashboard: ITemplate;
  @ViewChild(TemplatesDirective) childTemplateTag: TemplatesDirective;

  constructor(componentFactoryResolver: ComponentFactoryResolver, cdr: ChangeDetectorRef) {
    super(componentFactoryResolver, cdr);
  }

  ngOnInit() {
    // get template type for dashboard and create template component
    // noinspection TypeScriptValidateTypes
    // noinspection TypeScriptValidateTypes
   // let templateName = this.temaDashboard.template;
    this.baseTemplate = CaponeTemplateComponent;

    // noinspection TypeScriptValidateTypes
    this.widgets = [
      {
        component: BuildWidgetComponent,
        status: 'Success'
      },
      {
        component: BuildWidgetComponent,
        status: 'Success'
      }
      ,
      {
        component: BuildWidgetComponent,
        status: 'Success'
      },
      {
        component: BuildWidgetComponent,
        status: 'Success'
      },
      {
        component: BuildWidgetComponent,
        status: 'Success'
      },
      {
        component: BuildWidgetComponent,
        status: 'Success'
      }
    ];
  }

  ngAfterViewInit() {
    super.loadComponent(this.childTemplateTag);
  }

}


