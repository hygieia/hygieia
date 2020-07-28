import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { map, switchMap, take } from 'rxjs/operators';
import { CollectorService } from 'src/app/shared/collector.service';
import { DashboardService } from 'src/app/shared/dashboard.service';

@Component({
  selector: 'app-build-delete-form',
  templateUrl: './build-delete-form.component.html',
  styleUrls: ['./build-delete-form.component.scss']
})
export class BuildDeleteFormComponent implements OnInit {

  // buttons
  public confirm = 'Confirm';
  public cancel = 'Cancel';
  @Input() public message = 'This build item will be deleted immediately. Would you like to delete?';

  widgetConfigId: string;
  private componentId: string;

  buildDeleteForm: FormGroup;

  @Input()
  set widgetConfig(widgetConfig: any) {
    if (!widgetConfig) {
      return;
    }
    this.widgetConfigId = widgetConfig.options.id;
    this.buildDeleteForm.get('buildDurationThreshold').setValue(widgetConfig.options.buildDurationThreshold);
    this.buildDeleteForm.get('consecutiveFailureThreshold').setValue(widgetConfig.options.consecutiveFailureThreshold);
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
    this.getSavedBuildJob();
    this.getDashboardComponent();
  }

  public createDeleteForm() {
    this.buildDeleteForm = this.formBuilder.group({
        buildDurationThreshold: '',
        consecutiveFailureThreshold: '',
        buildJob: ''
    });
  }

  public getSavedBuildJob() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        const buildCollector = dashboard.application.components[0].collectorItems.Build;
        const savedCollectorBuildJob = buildCollector ? buildCollector[0].description : null;

        if (savedCollectorBuildJob) {
          const buildId = buildCollector[0].id;
          return buildId;
        }
        return null;
      }),
      switchMap(buildId => {
        if (buildId) {
          return this.collectorService.getItemsById(buildId);
        }
        return of(null);
      })).subscribe(collectorData => {
      this.buildDeleteForm.get('buildJob').setValue(collectorData);
    });
  }

  public submitDeleteForm() {
    const deleteConfig = {
      name: 'build',
      options: {
        id: this.widgetConfigId,
        buildDurationThreshold: +this.buildDeleteForm.value.buildDurationThreshold,
        consecutiveFailureThreshold: +this.buildDeleteForm.value.consecutiveFailureThreshold
      },
      componentId: this.componentId,
      collectorItemId: this.buildDeleteForm.value.buildJob.id
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
