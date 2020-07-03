import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ComponentFactoryResolver, EventEmitter,
  OnInit, Output,
  ViewChild
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DashboardService } from 'src/app/shared/dashboard.service';
import { DashboardComponent } from 'src/app/shared/dashboard/dashboard.component';
import { TemplatesDirective } from 'src/app/shared/templates/templates.directive';
import { CaponeTemplateComponent } from '../capone-template/capone-template.component';
import { widgetsAll } from './dashboard-view';
import {IWidget} from '../../../shared/interfaces';

@Component({
  selector: 'app-dashboard-view',
  templateUrl: './dashboard-view.component.html',
  styleUrls: ['./dashboard-view.component.scss']
})
export class DashboardViewComponent extends DashboardComponent implements OnInit, AfterViewInit {

  @Output() title = new EventEmitter(true);
  dashboardTitle = '';
  dashboardId: string;
  widgetsAll: IWidget[] = widgetsAll;
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
  }

  ngAfterViewInit() {
    this.loadWidgets();
  }

  private loadWidgets() {
    this.dashboardService.dashboardConfig$.subscribe(dashboard => {
      this.dashboardTitle = [dashboard.title, dashboard.configurationItemBusAppName, dashboard.configurationItemBusServName]
        .filter(Boolean).join(' - ');

      const activeWidgets = new Set<string>();
      dashboard.widgets.forEach(widget => activeWidgets.add(widget.name));
      if (dashboard.activeWidgets && dashboard.activeWidgets.length) {
        dashboard.activeWidgets.forEach(wName => activeWidgets.add(wName));
      }
      const widgets: IWidget[] = [];
      activeWidgets.forEach(widgetName => {
        const fWidget = this.widgetsAll.find(widget =>
          widget.title.join().toLowerCase().replace(/\s/g, '').includes(widgetName));
        if (fWidget) {
          widgets.push(fWidget);
        }
      });
      this.widgets = widgets;
      super.loadComponent(this.childTemplateTag);
    });
  }
}
