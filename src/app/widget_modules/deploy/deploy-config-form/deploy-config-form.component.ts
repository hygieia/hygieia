import { Component, OnInit, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import {catchError, debounceTime, distinctUntilChanged, map, switchMap, take, tap} from 'rxjs/operators';
import { CollectorService } from 'src/app/shared/collector.service';
import { DashboardService } from 'src/app/shared/dashboard.service';

@Component({
  selector: 'app-deploy-config-form',
  templateUrl: './deploy-config-form.component.html',
  styleUrls: ['./deploy-config-form.component.scss']
})
export class DeployConfigFormComponent implements OnInit {

  private widgetConfigId: string;
  private componentId: string;

  deployConfigForm: FormGroup;
  searching = false;
  searchFailed = false;
  typeAheadResults: (text$: Observable<string>) => Observable<any>;

  getDeployTitle = (deployItem: any) => {
    if (!deployItem) {
      return '';
    }
    const description = (deployItem.description as string);
    return deployItem.niceName + ' : ' + description;
  }

  @Input()
  set widgetConfig(widgetConfig) {
    this.widgetConfigId = widgetConfig.options.id;

    if (widgetConfig.options.deployRegex !== undefined && widgetConfig.options.deployRegex !== null) {
      this.deployConfigForm.get('deployRegex').setValue(widgetConfig.options.deployRegex);
    }
    if (widgetConfig.options.deployAggregateServer) {
      this.deployConfigForm.get('deployAggregateServer').setValue(widgetConfig.options.deployAggregateServer);
    } else {
      this.deployConfigForm.get('deployAggregateServer').setValue(false);
    }
  }

  constructor(
    public activeModal: NgbActiveModal,
    private formBuilder: FormBuilder,
    private collectorService: CollectorService,
    private dashboardService: DashboardService
  ) {
    this.createForm();
  }

  ngOnInit() {
    this.typeAheadResults = (text$: Observable<string>) =>
      text$.pipe(
        debounceTime(300),
        distinctUntilChanged(),
        tap(() => this.searching = true),
        switchMap(term => {
          return term.length < 2 ? of([]) :
            this.collectorService.searchItems('build', term).pipe(
              tap(() => this.searchFailed = false),
              catchError(() => {
                this.searchFailed = true;
                return of([]);
              }));
        }),
        tap(() => this.searching = false)
      );

    this.loadSavedDeployJobs();
    this.getDashboardComponent();
  }
  private createForm() {
    this.deployConfigForm = this.formBuilder.group({
      deployDurationThreshold: ['', Validators.required],
      consecutiveFailureThreshold: '',
      deployRegex: [''],
      deployJob: [''],
      deployAggregateServer: Boolean
    });
  }

  private submitForm() {
    const newConfig = {
      name: 'deploy',
      options: {
        id: this.widgetConfigId,
        deployRegex: this.deployConfigForm.value.deployRegex,
        deployAggregateServer: this.deployConfigForm.value.deployAggregateServer
      },
      componentId: this.componentId,
      collectorItemId: this.deployConfigForm.value.deployJob.id
    };
    this.activeModal.close(newConfig);
  }

  private loadSavedDeployJobs() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        const deployCollector = dashboard.application.components[0].collectorItems.Deployment;
        const savedCollectorDeploymentJob = deployCollector ? deployCollector[0].description : null;
        if (savedCollectorDeploymentJob) {
          const deployId = deployCollector[0].id;
          return deployId;
        }
        return null;
      }),
      switchMap(deployId => {
        if (deployId) {
          return this.collectorService.getItemsById(deployId);
        }
        return of(null);
      })).subscribe(collectorData => {
        this.deployConfigForm.get('deployJob').setValue(collectorData);
    });
  }
  private getDashboardComponent() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        return dashboard.application.components[0].id;
      })).subscribe(componentId => this.componentId = componentId);
  }
}

