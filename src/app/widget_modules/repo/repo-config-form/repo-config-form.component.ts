import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { map, take } from 'rxjs/operators';
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
    this.repoConfigForm.get('scm').setValue(widgetConfig.options.scm.name);
    this.repoConfigForm.get('url').setValue(widgetConfig.options.url);
    this.repoConfigForm.get('branch').setValue(widgetConfig.options.branch);
    this.repoConfigForm.get('userID').setValue(widgetConfig.options.userID);
    this.repoConfigForm.get('password').setValue(widgetConfig.options.password);
    this.repoConfigForm.get('personalAccessToken').setValue(widgetConfig.options.personalAccessToken);
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
    this.getDashboardComponent();
  }

  public createForm() {
    this.repoConfigForm = this.formBuilder.group({
      scm: '',
      url: '',
      branch: '',
      userID: '',
      password: '',
      personalAccessToken: ''
    });
  }

  public submitForm() {
    const newConfig = {
      name: 'repo',
      componentId: this.componentId,
      options: {
        id: this.widgetConfigId ? this.widgetConfigId : 'repo0',
        scm: {
          name: this.repoConfigForm.value.scm,
          value: this.repoConfigForm.value.scm,
        },
        url: this.repoConfigForm.value.url,
        branch: this.repoConfigForm.value.branch,
        userID: this.repoConfigForm.value.userID,
        password: this.repoConfigForm.value.password,
        personalAccessToken: this.repoConfigForm.value.personalAccessToken
      },
    };
    this.activeModal.close(newConfig);
  }

  private getDashboardComponent() {
    this.dashboardService.dashboardConfig$.pipe(take(1),
      map(dashboard => {
        return dashboard.application.components[0].id;
      })).subscribe(componentId => this.componentId = componentId);
  }
}
