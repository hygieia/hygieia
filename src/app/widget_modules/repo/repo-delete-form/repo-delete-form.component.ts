import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { map, take } from 'rxjs/operators';
import { CollectorService } from 'src/app/shared/collector.service';
import { DashboardService } from 'src/app/shared/dashboard.service';

@Component({
  selector: 'app-repo-delete-form',
  templateUrl: './repo-delete-form.component.html',
  styleUrls: ['./repo-delete-form.component.scss']
})
export class RepoDeleteFormComponent implements OnInit {

  // buttons
  public confirm = 'Confirm';
  public cancel = 'Cancel';
  @Input() public message = 'This repo item will be deleted immediately. Would you like to delete?';

  widgetConfigId: string;
  private componentId: string;

  repoDeleteForm: FormGroup;

  @Input()
  set widgetConfig(widgetConfig: any) {
    if (!widgetConfig) {
      return;
    }
    this.widgetConfigId = widgetConfig.options.id;
    this.repoDeleteForm.get('scm').setValue(widgetConfig.options.scm);
    this.repoDeleteForm.get('url').setValue(widgetConfig.options.url);
    this.repoDeleteForm.get('branch').setValue(widgetConfig.options.branch);
    this.repoDeleteForm.get('userID').setValue(widgetConfig.options.userID);
    this.repoDeleteForm.get('password').setValue(widgetConfig.options.password);
    this.repoDeleteForm.get('personalAccessToken').setValue(widgetConfig.options.personalAccessToken);
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
    this.getDashboardComponent();
  }

  public createDeleteForm() {
    this.repoDeleteForm = this.formBuilder.group({
      scm: '',
      url: '',
      branch: '',
      userID: '',
      password: '',
      personalAccessToken: ''
    });
  }

  public submitDeleteForm() {
    const deleteConfig = {
      name: 'repo',
      componentId: this.componentId,
      options: {
        id: this.widgetConfigId,
        scm: this.repoDeleteForm.value.scm,
        url: this.repoDeleteForm.value.url,
        branch: this.repoDeleteForm.value.branch,
        userID: this.repoDeleteForm.value.userID,
        password: this.repoDeleteForm.value.password,
        personalAccessToken: this.repoDeleteForm.value.personalAccessToken
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
