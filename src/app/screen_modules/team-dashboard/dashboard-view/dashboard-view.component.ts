import { AfterViewInit, ChangeDetectorRef, Component, ComponentFactoryResolver, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DashboardService } from 'src/app/shared/dashboard.service';
import { DashboardComponent } from 'src/app/shared/dashboard/dashboard.component';
import { TemplatesDirective } from 'src/app/shared/templates/templates.directive';
import { PlaceholderWidgetComponent } from 'src/app/shared/widget/placeholder-widget/placeholder-widget.component';
import { BuildWidgetComponent } from 'src/app/widget_modules/build/build-widget/build-widget.component';
import { DeployWidgetComponent } from 'src/app/widget_modules/deploy/deploy-widget/deploy-widget.component';
import { CaponeTemplateComponent } from '../capone-template/capone-template.component';
import { ITemplate } from './dashboard-view';
import {Placeholder} from '@angular/compiler/src/i18n/i18n_ast';
import {BuildConfigFormComponent} from '../../../widget_modules/build/build-config-form/build-config-form.component';
import {DeployConfigFormComponent} from 'src/app/widget_modules/deploy/deploy-config-form/deploy-config-form.component';
import {RepoConfigFormComponent} from '../../../widget_modules/repo/repo-config-form/repo-config-form.component';
import {RepoWidgetComponent} from '../../../widget_modules/repo/repo-widget/repo-widget.component';
import {FeatureWidgetComponent} from '../../../widget_modules/feature/feature-widget/feature-widget.component';
import {FeatureConfigFormComponent} from '../../../widget_modules/feature/feature-config-form/feature-config-form.component';
import {
  StaticAnalysisWidgetComponent
} from '../../../widget_modules/static-analysis/static-analysis-widget/static-analysis-widget.component';
import {
  StaticAnalysisConfigFormComponent
} from '../../../widget_modules/static-analysis/static-anaylsis-config-form/static-analysis-config-form.component';

@Component({
  selector: 'app-dashboard-view',
  templateUrl: './dashboard-view.component.html',
  styleUrls: ['./dashboard-view.component.scss']
})
export class DashboardViewComponent extends DashboardComponent implements OnInit, AfterViewInit {

  teamDashboard: ITemplate;
  dashboardId: string;
  @ViewChild(TemplatesDirective, {static: false}) childTemplateTag: TemplatesDirective;

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
        title: 'Feature',
        component: FeatureWidgetComponent,
        status: 'Success',
        widgetSize: 'col-xl-3',
        configForm: FeatureConfigFormComponent
      },
      {
        title: 'Build',
        component: BuildWidgetComponent,
        status: 'Success',
        widgetSize: 'col-xl-6',
        configForm: BuildConfigFormComponent
      },
      {
        title: 'Deploy',
        component: DeployWidgetComponent,
        status: 'Success',
        widgetSize: 'col-xl-3',
        configForm: DeployConfigFormComponent
      },
      {
        title: 'Repo',
        component: RepoWidgetComponent,
        status: 'Success',
        widgetSize: 'col-xl-4',
        configForm: RepoConfigFormComponent
      },
      {
        title: 'Static Code Analysis',
        component: StaticAnalysisWidgetComponent,
        status: 'Success',
        widgetSize: 'col-xl-4',
        configForm: StaticAnalysisConfigFormComponent
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

