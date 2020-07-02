import {Component, OnInit, Output} from '@angular/core';
import {DashboardService} from '../../shared/dashboard.service';
import {Router} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';
import {NbDialogRef} from '@nebular/theme';

class Widget {
  name: string;
  status: boolean;
  constructor(name: string, status: boolean) {
    this.name = name;
    this.status = status;
  }
}

class CTemplate {
  name: string;
  status: boolean;
  constructor(name: string, status: boolean) {
    this.name = name;
    this.status = status;
  }
}

@Component({
  selector: 'app-dashboard-create',
  templateUrl: './dashboard-create.component.html',
  styleUrls: ['./dashboard-create.component.scss']
})
export class DashboardCreateComponent implements OnInit {

  selectedLayoutItems: string[] = [];
  @Output() title: string;
  appName: string;
  busService: string;
  busApp: string;
  next: any;
  createErrorMsg: string;
  dType = 'Team';
  dLayout = 'Templates';
  widgetNames: Array<string> = ['build', 'codeanalysis', 'deploy', 'feature', 'repo'];
  templateNames: Array<string> = ['CapOne'];
  widgets: Widget[] = [];
  templates: CTemplate[] = [];

  constructor(private dashboardService: DashboardService, private router: Router,
              private dialogRef: NbDialogRef<any>) {}

  isValidTitle = (title: string) => (title && title.trim().length >= 6);
  isValidAppName = (appName: string) => (appName && appName.trim().length >= 6);
  isValidType = (dType: any) => dType;
  isValidTemplate = (template: any) => template;

  ngOnInit() {
    this.widgetNames.forEach(name => this.widgets.push(new Widget(name, false)));
    this.templateNames.forEach(name => this.templates.push(new CTemplate(name, false)));
  }

  createDashboard() {
    this.selectedLayoutItems = this.getSelectedLayoutItems();
    const submitData = {
      template: this.dLayout,
      title: this.title,
      type: this.dType,
      applicationName: this.appName,
      componentName: this.appName,
      configurationItemBusServName: this.busService,
      configurationItemBusAppName: this.busApp,
      scoreEnabled : false,
      scoreDisplay : false,
      activeWidgets: this.selectedLayoutItems
    };
    this.dashboardService.createDashboard(submitData).subscribe(response => {
      this.close();
      if (response instanceof HttpErrorResponse) {
        this.createErrorMsg = response.message;
        return;
      }
      this.router.navigate([`dashboard/dashboardview/${response.id}`, { activeWidgets: this.selectedLayoutItems}]);
    });
  }

  onSubmit() {
  }

  close() {
    this.dialogRef.close();
  }

  private getSelectedLayoutItems(): string[] {
    if (this.dLayout === 'Widgets') {
      return this.getSelectedWidgets();
    }
    if (this.dLayout === 'Templates') {
      return this.getSelectedTemplate();
    }
  }

  private getSelectedWidgets(): string[] {
    const selectedWidgets: string[] = this.widgets.filter(widget => widget.status === true).map(widget => widget.name);
    if (!selectedWidgets || selectedWidgets.length === 0) {
      selectedWidgets.push(...this.widgetNames);
    }
    return selectedWidgets;
  }

  private getSelectedTemplate(): string[] {
    const selectedTemplate: CTemplate = this.templates.find(template => template.status === true);
    if (selectedTemplate.name === 'CapOne') {
      return this.widgetNames;
    }
  }
}
