import { Component, OnInit, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of, from } from 'rxjs';
import {debounceTime, distinctUntilChanged, last, map, switchMap, take, tap} from 'rxjs/operators';
import { CollectorService } from 'src/app/shared/collector.service';
import { DashboardService } from 'src/app/shared/dashboard.service';
import * as _ from 'lodash';
import {runFilenameOrFn_} from 'protractor/built/util';

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

  getDeploysCallback = (data) => {
    this.deployConfigForm.value.deployJob = data[0];
    this.deployConfigForm.get('deployJob').setValue(data[0]);
  }

  getDeployTitle = (deployJob: any) => {
    if (!deployJob) {
      return '';
    }
    return deployJob.name;
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
            from(this.getDeploymentJobs(term));
        }),
        tap(() => this.searching = false)
      );

    this.getDashboardComponent();
    this.loadSavedDeployment();
  }
  private createForm() {
    this.deployConfigForm = this.formBuilder.group({
      deployDurationThreshold: ['', Validators.required],
      consecutiveFailureThreshold: '',
      deployRegex: [''],
      deployJob: ['', Validators.required],
      deployAggregateServer: Boolean
    });
  }

  public getDeploymentJobs(filter) {
    return this.getDeploymentJobsRecursive([], filter, null, 0).then(this.processResponse);
  }

  private processResponse(data: any[]) {
    const dataGrouped = _.chain(data[0]).groupBy(function (d) {
      // tslint:disable-next-line:forin
      return ('') + d.options.applicationName + d.options.applicationId;
    }).map(function (d) {
      return d;
    }).value();
    const deploys = _.chain(dataGrouped).map(function (deploys, idx) {
      const firstDeploy = deploys[0];
      const name = firstDeploy.options.applicationName;
      let group = '';
      const ids = new Array(deploys.length);
      deploys.forEach ((deploy) => {
        ids.push(deploy.id);
        if (group !== '') {
          group += '\n';
        }
        group += ((deploy.niceName !== null) && (deploy.niceName !== '') ? deploy.niceName : deploy.collector.name) +
          ' (' + deploy.options.instanceUrl + ')';
      });

      return {
        value: ids,
        name: name,
        group: group
      };
    }).value();
    return deploys;
  }

  private getDashboardComponent() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        return dashboard.application.components[0].id;
      })).subscribe(componentId => this.componentId = componentId);
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

  private testResponse(arr, response, nameAndIdToCheck) {
    if (response !== undefined && response !== null) {
      arr.push(response as any[]);
      arr.push.apply(arr, _.chain(response).filter((d) => {
        return nameAndIdToCheck === null;
      }).value());
    }
    return arr;
  }

  private getDeploymentJobsRecursive(arr: any[], filter, nameAndIdToCheck, pageNumber) {
    const params = {search: filter, size: 20, sort: 'description', page: pageNumber};
    const responsePromise = this.collectorService.getItemsByType('deployment', params).toPromise();
    return responsePromise.then(this.testResponse(arr, responsePromise, nameAndIdToCheck));
  }

  private loadSavedDeployment() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        const deployCollector = dashboard.application.components[0].collectorItems.Deployment;
        const savedCollectorDeploymentJob = deployCollector ? deployCollector[0].description : null;
        if (savedCollectorDeploymentJob) {
          this.getDeploymentJobs(savedCollectorDeploymentJob).then(this.getDeploysCallback);
        }
      })).subscribe();
  }
}
