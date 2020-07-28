import { Component, Input, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { map, switchMap, take } from 'rxjs/operators';
import { CollectorService } from 'src/app/shared/collector.service';
import { DashboardService } from 'src/app/shared/dashboard.service';

@Component({
  selector: 'app-oss-delete-form',
  templateUrl: './oss-delete-form.component.html',
  styleUrls: ['./oss-delete-form.component.scss']
})
export class OSSDeleteFormComponent implements OnInit {

  // buttons
  public confirm = 'Confirm';
  public cancel = 'Cancel';
  @Input() public message = 'This OSS item will be deleted immediately. Would you like to delete?';

  widgetConfigId: string;
  private componentId: string;

  ossDeleteForm: FormGroup;

  @Input()
  set widgetConfig(widgetConfig: any) {
    if (!widgetConfig) {
      return;
    }
    this.widgetConfigId = widgetConfig.options.id;
    this.ossDeleteForm.get('ossJob').setValue(widgetConfig.collectorItemId);
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
    this.getSavedOSSJob();
    this.getDashboardComponent();
  }

  public createDeleteForm() {
    this.ossDeleteForm = this.formBuilder.group({
      ossJob: ['', Validators.required]
    });
  }

  public getSavedOSSJob() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        const ossCollector = dashboard.application.components[0].collectorItems.LibraryPolicy;
        const savedCollectorOSS = ossCollector ? ossCollector[0].description : null;

        if (savedCollectorOSS) {
          const ossId = ossCollector[0].id;
          return ossId;
        }
        return null;
      }),
      switchMap(ossId => {
        if (ossId) {
          return this.collectorService.getItemsById(ossId);
        }
        return of(null);
      })).subscribe(collectorData => {
      this.ossDeleteForm.get('ossJob').setValue(collectorData);
    });
  }

  public submitDeleteForm() {
    const deleteConfig = {
      name: 'codeanalysis',
      options: {
        id: this.widgetConfigId,
      },
      componentId: this.componentId,
      collectorItemId: this.ossDeleteForm.value.ossJob.id
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
