import { Component, Input, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { map, switchMap, take } from 'rxjs/operators';
import { CollectorService } from 'src/app/shared/collector.service';
import { DashboardService } from 'src/app/shared/dashboard.service';

@Component({
  selector: 'app-security-scan-delete-form',
  templateUrl: './security-scan-delete-form.component.html',
  styleUrls: ['./security-scan-delete-form.component.scss']
})
export class SecurityScanDeleteFormComponent implements OnInit {

  // buttons
  public confirm = 'Confirm';
  public cancel = 'Cancel';
  @Input() public message = 'This security scan item will be deleted immediately. Would you like to delete?';

  private componentId: string;
  widgetConfigId: string;

  securityDeleteForm: FormGroup;

  @Input()
  set widgetConfig(widgetConfig: any) {
    if (!widgetConfig) {
      return;
    }
    this.widgetConfigId = widgetConfig.options.id;
    this.securityDeleteForm.get('sJob').setValue(widgetConfig.options.sJob);
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
    this.getSavedSecurityJob();
    this.getDashboardComponent();
  }

  public createDeleteForm() {
    this.securityDeleteForm = this.formBuilder.group({
      sJob: ['', Validators.required]
    });
  }

  public getSavedSecurityJob() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        const securityCollector = dashboard.application.components[0].collectorItems.StaticSecurityScan;
        const savedCollectorSecurityJob = securityCollector ? securityCollector[0].description : null;

        if (savedCollectorSecurityJob) {
          const securityId = securityCollector[0].id;
          return securityId;
        }
        return null;
      }),
      switchMap(securityId => {
        if (securityId) {
          return this.collectorService.getItemsById(securityId);
        }
        return of(null);
      })).subscribe(collectorData => {
      if (collectorData) {
        this.securityDeleteForm.get('sJob').setValue(collectorData);
      }
    });
  }

  public submitDeleteForm() {
    const deleteConfig = {
      name: 'codeanalysis',
      componentId: this.componentId,
      collectorItemId: this.securityDeleteForm.value.sJob.id,
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
