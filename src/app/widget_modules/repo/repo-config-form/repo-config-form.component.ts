import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { map, switchMap, take } from 'rxjs/operators';
import { CollectorService } from 'src/app/shared/collector.service';
import { DashboardService } from 'src/app/shared/dashboard.service';

@Component({
  selector: 'app-repo-config-form',
  templateUrl: './repo-config-form.component.html',
  styleUrls: ['./repo-config-form.component.scss']
})
export class RepoConfigFormComponent implements OnInit {

  private widgetConfigId: string;
  private componentId: string;

  repoConfigForm: FormGroup;

  @Input()
  set widgetConfig(widgetConfig: any) {
    if (!widgetConfig) {
      return;
    }
    this.widgetConfigId = widgetConfig.options.id;
    this.repoConfigForm.get('type').setValue(widgetConfig.options.type);
    this.repoConfigForm.get('url').setValue(widgetConfig.options.url);
    this.repoConfigForm.get('branch').setValue(widgetConfig.options.branch);
    this.repoConfigForm.get('username').setValue(widgetConfig.options.username);
    this.repoConfigForm.get('password').setValue(widgetConfig.options.password);
    this.repoConfigForm.get('personalAccessToken').setValue(widgetConfig.options.personalAccessToken);
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
    this.loadSavedRepoJob();
    this.getDashboardComponent();
  }

  private createForm() {
    this.repoConfigForm = this.formBuilder.group({
      type: '',
      url: '',
      branch: '',
      username: '',
      password: '',
      personalAccessToken: ''
    });
  }

  private submitForm() {
    const newConfig = {
      name: 'repo',
      componentId: this.componentId,
      options: {
        id: this.widgetConfigId,
        type: this.repoConfigForm.value.type,
        url: this.repoConfigForm.value.url,
        branch: this.repoConfigForm.value.branch,
        username: this.repoConfigForm.value.username,
        password: this.repoConfigForm.value.password,
        personalAccessToken: this.repoConfigForm.value.personalAccessToken
      },
    };
    this.activeModal.close(newConfig);
  }

  private loadSavedRepoJob() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        const repoCollector = dashboard.application.components[0].collectorItems.SCM;

        if (repoCollector[0].id) {
          const repoId = repoCollector[0].id;
          return repoId;
        }
        return null;
      }),
      switchMap(repoId => {
        if (repoId) {
          return this.collectorService.getItemsById(repoId);
        }
        return of(null);
      })).subscribe(collectorData => {
      this.repoConfigForm.get('type').setValue(collectorData.collector.name);
      this.repoConfigForm.get('url').setValue(collectorData.options.url);
      this.repoConfigForm.get('branch').setValue(collectorData.options.branch);
      this.repoConfigForm.get('username').setValue(collectorData.options.userId);
      this.repoConfigForm.get('password').setValue(collectorData.options.password);
      this.repoConfigForm.get('personalAccessToken').setValue(collectorData.options.personalAccessToken);
    });
  }

  private getDashboardComponent() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        return dashboard.application.components[0].id;
      })).subscribe(componentId => this.componentId = componentId);
  }
}
