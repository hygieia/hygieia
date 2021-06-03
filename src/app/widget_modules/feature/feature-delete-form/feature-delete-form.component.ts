import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { map, switchMap, take } from 'rxjs/operators';
import { CollectorService } from 'src/app/shared/collector.service';
import { DashboardService } from 'src/app/shared/dashboard.service';

@Component({
  selector: 'app-feature-delete-form',
  templateUrl: './feature-delete-form.component.html',
  styleUrls: ['./feature-delete-form.component.scss']
})
export class FeatureDeleteFormComponent implements OnInit {

  // buttons
  public confirm = 'Confirm';
  public cancel = 'Cancel';
  @Input() public message = 'This feature item will be deleted immediately. Would you like to delete?';

  widgetConfigId: string;
  private componentId: string;
  public teamId: string;
  public projectId: string;

  featureDeleteForm: FormGroup;

  @Input()
  set widgetConfig(widgetConfig: any) {
    if (!widgetConfig) {
      return;
    }
    this.widgetConfigId = widgetConfig.options.id;
    this.featureDeleteForm.get('featureTool').setValue(widgetConfig.options.featureTool);
    this.featureDeleteForm.get('sprintType').setValue(widgetConfig.options.sprintType);
    this.featureDeleteForm.get('listType').setValue(widgetConfig.options.listType);
    this.featureDeleteForm.get('estimateMetricType').setValue(widgetConfig.options.estimateMetricType);
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
    this.getSavedFeatures();
    this.getDashboardComponent();
  }

  public createDeleteForm() {
    this.featureDeleteForm = this.formBuilder.group({
      featureTool: '',
      projectName: '',
      teamName: '',
      sprintType: '',
      listType: '',
      estimateMetricType: '',
    });
  }

  public getSavedFeatures() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        const featureCollector = dashboard.application.components[0].collectorItems.AgileTool;

        if (featureCollector[0].id) {
          const featureId = featureCollector[0].id;
          return featureId;
        }
        return null;
      }),
      switchMap(featureId => {
        if (featureId) {
          return this.collectorService.getItemsById(featureId);
        }
        return of(null);
      })).subscribe(collectorData => {
      this.teamId = collectorData.options.teamId;
      this.projectId = collectorData.options.projectId;
      this.featureDeleteForm.get('projectName').setValue(collectorData);
      this.featureDeleteForm.get('teamName').setValue(collectorData);
    });
  }

  public submitDeleteForm() {
    const deleteConfig = {
      name: 'feature',
      options: {
        id: this.widgetConfigId,
        featureTool: this.featureDeleteForm.value.featureTool,
        teamName: this.featureDeleteForm.value.teamName.options.teamName,
        teamId: this.teamId,
        projectName: this.featureDeleteForm.value.projectName.options.projectName,
        projectId: this.projectId,
        estimateMetricType: this.featureDeleteForm.value.estimateMetricType,
        sprintType: this.featureDeleteForm.value.sprintType,
        listType: this.featureDeleteForm.value.listType,
      },
      componentId: this.componentId,
      collectorItemId: this.featureDeleteForm.value.projectName.id
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
