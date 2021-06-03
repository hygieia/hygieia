import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {CollectorService} from '../../../shared/collector.service';
import {DashboardService} from '../../../shared/dashboard.service';
import {map, switchMap, take} from 'rxjs/operators';
import {of} from 'rxjs';

@Component({
  selector: 'app-infra-scan-delete',
  templateUrl: './infra-scan-delete.component.html',
  styleUrls: ['./infra-scan-delete.component.sass']
})
export class InfraScanDeleteComponent implements OnInit {

  // buttons
  public confirm = 'Confirm';
  public cancel = 'Cancel';
  @Input() public message = 'This infrastructure scan item will be deleted immediately. Would you like to delete?';

  private componentId: string;
  widgetConfigId: string;
  infraScanDeleteForm: FormGroup;

  @Input()
  set widgetConfig(widgetConfig: any) {
    if (!widgetConfig) {
      return;
    }
    this.widgetConfigId = widgetConfig.options.id;
    this.infraScanDeleteForm.get('iJob').setValue(widgetConfig.options.iJob);
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
    this.loadSavedInfraScanJob();
    this.getDashboardComponent();
  }

  public createDeleteForm() {
    this.infraScanDeleteForm = this.formBuilder.group({
      iJob: ['', Validators.required]
    });
  }

  loadSavedInfraScanJob() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        const infraScanCollector = dashboard.application.components[0].collectorItems.InfrastructureScan;
        const savedCollectorinfraScanJob = infraScanCollector ? infraScanCollector : null;

        if (savedCollectorinfraScanJob) {
          const infraScanId = savedCollectorinfraScanJob[0].id;
          return infraScanId;
        }
        return null;
      }),
      switchMap(infraScanId => {
        if (infraScanId) {
          return this.collectorService.getItemsById(infraScanId);
        }
        return of(null);
      })).subscribe(collectorData => {
      if (collectorData) {
        this.infraScanDeleteForm.get('iJob').setValue(collectorData);
      }
    });
  }

  public submitForm() {
    const deleteConfig = {
      name: 'infrascan',
      componentId: this.componentId,
      collectorItemId: this.infraScanDeleteForm.value.iJob.id,
      options: {
        id: this.widgetConfigId,
      },
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
