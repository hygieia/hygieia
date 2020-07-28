import { Component, Input, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { map, switchMap, take } from 'rxjs/operators';
import { CollectorService } from 'src/app/shared/collector.service';
import { DashboardService } from 'src/app/shared/dashboard.service';

@Component({
  selector: 'app-static-analysis-delete-form',
  templateUrl: './static-analysis-delete-form.component.html',
  styleUrls: ['./static-analysis-delete-form.component.scss']
})
export class StaticAnalysisDeleteFormComponent implements OnInit {

  // buttons
  public confirm = 'Confirm';
  public cancel = 'Cancel';
  @Input()
  public message =
    'This static analysis item will be deleted immediately. Would you like to delete?';

  private componentId: string;
  widgetConfigId: string;

  staticAnalysisDeleteForm: FormGroup;

  @Input()
  set widgetConfig(widgetConfig: any) {
    if (!widgetConfig) {
      return;
    }
    this.widgetConfigId = widgetConfig.options.id;
    this.staticAnalysisDeleteForm.get('staticAnalysisJob').setValue(widgetConfig.collectorItemId);
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
    this.getSavedCodeQualityJob();
    this.getDashboardComponent();
  }

  public createDeleteForm() {
    this.staticAnalysisDeleteForm = this.formBuilder.group({
      staticAnalysisJob: ['', Validators.required]
    });
  }

  public getSavedCodeQualityJob() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        const sonarCollector = dashboard.application.components[0].collectorItems.CodeQuality;
        const savedCollectorCodeQuality = sonarCollector ? sonarCollector[0].description : null;

        if (savedCollectorCodeQuality) {
          const codeQualityId = sonarCollector[0].id;
          return codeQualityId;
        }
        return null;
      }),
      switchMap(codeQualityId => {
        if (codeQualityId) {
          return this.collectorService.getItemsById(codeQualityId);
        }
        return of(null);
      })).subscribe(collectorData => {
      this.staticAnalysisDeleteForm.get('staticAnalysisJob').setValue(collectorData);
    });
  }

  public submitDeleteForm() {
    const deleteConfig = {
      name: 'codeanalysis',
      options: {
        id: this.widgetConfigId,
      },
      componentId: this.componentId,
      collectorItemId: this.staticAnalysisDeleteForm.value.staticAnalysisJob.id
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
