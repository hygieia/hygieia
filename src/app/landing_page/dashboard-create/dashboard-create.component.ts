import {Component, OnInit, Output} from '@angular/core';
import {DashboardService} from '../../shared/dashboard.service';
import {Router} from '@angular/router';
import {NbDialogRef} from '@nebular/theme';
import {Observable, of} from 'rxjs';
import {catchError, debounceTime, distinctUntilChanged, switchMap, tap} from 'rxjs/operators';
import {CmdbDataService} from '../../admin_modules/admin_dashboard/services/cmdb-data.service';

class Widget {
  name: string;
  status: boolean;
  constructor(name: string, status: boolean) {
    this.name = name;
    this.status = status;
  }
}

class DTemplate {
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
  searching = false;
  searchFailed = false;

  constructor(private dashboardService: DashboardService, private router: Router,
              private dialogRef: NbDialogRef<any>, private cmdbService: CmdbDataService) {}

  selectedLayoutItems: string[] = [];
  @Output() title: string;
  appName: string;
  busService: string;
  busApp: string;
  next: any;
  createErrorMsg: string;
  dType = 'Team';
  dLayout = 'Templates';
  widgetNames: Array<string> = ['feature', 'build', 'repo', 'codeanalysis', 'deploy'];
  templateNames: Array<string> = ['CapOne'];
  widgets: Widget[] = [];
  templates: DTemplate[] = [];
  isAnySelected: boolean;
  typeAheadBAItems: (text$: Observable<string>) => Observable<any>;
  typeAheadBCItems: (text$: Observable<string>) => Observable<any>;

  isValidTitle = (title: string) => (title && title.trim().length >= 6);
  isValidAppName = (appName: string) => (appName && appName.trim().length >= 6);
  isValidType = (dType: any) => dType;
  isValidTemplate = (template: any) => template;
  getBusService = (cmdb: any) => cmdb ? cmdb.configurationItem + ' : ' + cmdb.commonName as string : '';
  getBusServiceInput = (cmdb: any) => cmdb ? cmdb.configurationItem as string : '';
  getBusApp = (cmdb: any) => cmdb ? cmdb.configurationItem + ' : ' + cmdb.commonName as string : '';
  getBusAppInput = (cmdb: any) => cmdb ? cmdb.configurationItem as string : '';

  ngOnInit() {
    this.widgetNames.forEach(name => this.widgets.push(new Widget(name, false)));
    this.templateNames.forEach(name => this.templates.push(new DTemplate(name, false)));
    this.lookUpBusinessItems();
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
      this.router.navigate([`dashboard/dashboard-view/${response.id}`, { activeWidgets: this.selectedLayoutItems}]);
      this.close();
    }, error => { this.createErrorMsg = 'Error creating dashboard, invalid request'; });
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
    return [];
  }

  private getSelectedWidgets(): string[] {
    const selectedWidgets: string[] = this.widgets.filter(widget => widget.status === true).map(widget => widget.name);
    if (!selectedWidgets || selectedWidgets.length === 0) {
      selectedWidgets.push(...this.widgetNames);
    }
    return selectedWidgets;
  }

  private getSelectedTemplate(): string[] {
    const selectedTemplate: DTemplate = this.templates.find(template => template.status === true);
    if (selectedTemplate && selectedTemplate.name === 'CapOne') {
      return this.widgetNames;
    }
  }

  onClick(item: any) {
    let dItems: any[] = [];
    if (item instanceof Widget) {
      dItems = this.widgets;
    }
    if (item instanceof DTemplate) {
      this.templates.forEach(template => {
        if (template.name !== item.name) {
          template.status = false;
        }
      });
      dItems = this.templates;
    }
    this.isAnySelected = dItems.find(dItem => dItem.status === true);
  }

  clear() {
    this.isAnySelected = false;
    this.createErrorMsg = '';
    this.widgets.forEach(widget => widget.status = false);
    this.templates.forEach(template => template.status = false);
  }

  private lookUpBusinessItems() {
    this.typeAheadBAItems = (text$: Observable<string>) => this.getConfigItems(text$, 'app');
    this.typeAheadBCItems = (text$: Observable<string>) => this.getConfigItems(text$, 'component');
  }

  private getConfigItems(text$: Observable<string>, itemType: string): Observable<any> {
    return text$.pipe(
        debounceTime(100),
        distinctUntilChanged(),
        tap(() => this.searching = true),
        switchMap(term =>
            this.cmdbService.getConfigItems(itemType, { search: term, size: 10 }).pipe(
                tap(() => this.searchFailed = false),
                catchError(() => {
                  this.searchFailed = true;
                  return of([]);
                }))
        ),
        tap(() => this.searching = false)
    );
  }
}
