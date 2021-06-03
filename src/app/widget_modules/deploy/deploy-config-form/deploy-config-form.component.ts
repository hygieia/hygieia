import { Component, OnInit, Input } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
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

  getDeployJob = (deployItem: any) => {
    if (!deployItem) {
      return '';
    }
    const description = (deployItem.description as string);
    return description;
  }

  @Input()
  set widgetConfig(widgetConfig) {
    if (!widgetConfig) {
      return;
    }
    this.widgetConfigId = widgetConfig.options.id;
    this.deployConfigForm.get('deployRegex').setValue(widgetConfig.options.deployRegex);
    if (widgetConfig.options.deployAggregateServer) {
      this.deployConfigForm.get('deployAggregateServer').setValue(widgetConfig.options.deployAggregateServer);
    } else {
      this.deployConfigForm.get('deployAggregateServer').setValue(false);
    }
  }

  constructor(
    public activeModal: NgbActiveModal,
    public formBuilder: FormBuilder,
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
            this.collectorService.searchItemsBySearchField('Deployment', term, 'description').pipe(
              tap(val => {
                if (!val || val.length === 0) {
                  this.searchFailed = true;
                  return of([]);
                }
                this.searchFailed = false;
              }),
              catchError(() => {
                this.searchFailed = true;
                return of([]);
              })
            );
        }),
        tap(() => this.searching = false)
      );

    this.loadSavedDeployJobs();
    this.getDashboardComponent();
  }
  public createForm() {
    this.deployConfigForm = this.formBuilder.group({
      deployRegex: [''],
      deployJob: ['', Validators.required],
      deployAggregateServer: Boolean
    });
  }

  public submitForm() {
    if (this.deployConfigForm.invalid) {
      return;
    }

    const newConfig = {
      name: 'deploy',
      options: {
        id: this.widgetConfigId ? this.widgetConfigId : 'deploy0',
        deployRegex: this.deployConfigForm.value.deployRegex,
        deployAggregateServer: this.deployConfigForm.value.deployAggregateServer
      },
      componentId: this.componentId,
      collectorItemId: this.deployConfigForm.value.deployJob.id
    };
    this.activeModal.close(newConfig);
  }

  public loadSavedDeployJobs() {
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

  // convenience getter for easy access to form fields
  get configForm() { return this.deployConfigForm.controls; }
}
