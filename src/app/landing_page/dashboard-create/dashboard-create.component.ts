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

@Component({
  selector: 'app-dashboard-create',
  templateUrl: './dashboard-create.component.html',
  styleUrls: ['./dashboard-create.component.scss']
})
export class DashboardCreateComponent implements OnInit {

  selectedWidgets: string[] = [];
  @Output() title: string;
  appName: string;
  busService: string;
  busApp: string;
  next: any;
  createErrorMsg: string;
  dType = 'Team';
  dLayout = 'Widgets';
  widgetNames: Array<string> = ['build', 'codeanalysis', 'deploy', 'feature', 'repo'];
  widgets: Widget[] = [];

  constructor(private dashboardService: DashboardService, private router: Router,
              private dialogRef: NbDialogRef<any>) {}

  isValidTitle = (title: string) => (title && title.trim().length >= 6);
  isValidAppName = (appName: string) => (appName && appName.trim().length >= 6);
  isValidType = (dType: any) => dType;
  isValidTemplate = (template: any) => template;

  ngOnInit() {
    this.widgetNames.forEach(name => this.widgets.push(new Widget(name, false)));
  }

  createDashboard() {
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
      activeWidgets: this.getSelectedWidgets()
    };
    this.dashboardService.createDashboard(submitData).subscribe(response => {
      this.close();
      if (response instanceof HttpErrorResponse) {
        this.createErrorMsg = response.message;
        return;
      }
      this.router.navigate([`dashboard/dashboardview/${response.id}`, { activeWidgets: this.selectedWidgets}]);
    });
  }

  onSubmit() {
  }

  close() {
    this.dialogRef.close();
  }

  private getSelectedWidgets(): string[] {
    const selectedWidgets: string[] = [];
    this.widgets
      .filter(widget => widget.status === true)
      .forEach(widget => selectedWidgets.push(widget.name));
    if (selectedWidgets.length === 0) {
      selectedWidgets.push(...this.widgetNames);
    }
    return selectedWidgets;
  }
}
