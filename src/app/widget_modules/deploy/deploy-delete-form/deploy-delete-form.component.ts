import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { map, switchMap, take } from 'rxjs/operators';
import { CollectorService } from 'src/app/shared/collector.service';
import { DashboardService } from 'src/app/shared/dashboard.service';

@Component({
  selector: 'app-deploy-delete-form',
  templateUrl: './deploy-delete-form.component.html',
  styleUrls: ['./deploy-delete-form.component.scss']
})
export class DeployDeleteFormComponent implements OnInit {

  // buttons
  public confirm = 'Confirm';
  public cancel = 'Cancel';
  @Input() public message = 'This Deploy item will be deleted immediately. Would you like to delete?';

  widgetConfigId: string;
  private componentId: string;

  deployDeleteForm: FormGroup;

  @Input()
  set widgetConfig(widgetConfig) {
    if (!widgetConfig) {
      return;
    }
    this.widgetConfigId = widgetConfig.options.id;
    this.deployDeleteForm.get('deployRegex').setValue(widgetConfig.options.deployRegex);
    if (widgetConfig.options.deployAggregateServer) {
      this.deployDeleteForm.get('deployAggregateServer').setValue(widgetConfig.options.deployAggregateServer);
    } else {
      this.deployDeleteForm.get('deployAggregateServer').setValue(false);
    }
  }

  constructor(
    public activeModal: NgbActiveModal,
    public formBuilder: FormBuilder,
    public collectorService: CollectorService,
    public dashboardService: DashboardService
  ) {
    this.createDeleteForm();
  }

  ngOnInit() {
    this.getSavedDeployJob();
    this.getDashboardComponent();
  }

  public createDeleteForm() {
    this.deployDeleteForm = this.formBuilder.group({
      deployRegex: [''],
      deployJob: [''],
      deployAggregateServer: Boolean
    });
  }

  public getSavedDeployJob() {
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
      this.deployDeleteForm.get('deployJob').setValue(collectorData);
    });
  }

  public submitDeleteForm() {
    const deleteConfig = {
      name: 'deploy',
      options: {
        id: this.widgetConfigId,
        deployRegex: this.deployDeleteForm.value.deployRegex,
        deployAggregateServer: this.deployDeleteForm.value.deployAggregateServer
      },
      componentId: this.componentId,
      collectorItemId: this.deployDeleteForm.value.deployJob.id
    };
    this.activeModal.close(deleteConfig);
  }

  private getDashboardComponent() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        return dashboard.application.components[0].id;
      })).subscribe(componentId => this.componentId = componentId);
  }
}
