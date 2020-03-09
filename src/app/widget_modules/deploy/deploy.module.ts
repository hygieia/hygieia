import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { CommonModule } from '@angular/common';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { DeployConfigFormComponent } from './deploy-config-form/deploy-config-form.component';
import { DeployDetailComponent } from './deploy-detail/deploy-detail.component';
import { DeployWidgetComponent } from './deploy-widget/deploy-widget.component';

@NgModule({
  declarations: [DeployWidgetComponent, DeployConfigFormComponent, DeployDetailComponent],
  entryComponents: [DeployConfigFormComponent, DeployDetailComponent],
  imports: [
    CommonModule,
    NgbModule,
    SharedModule
  ],
  exports: []
})
export class DeployModule { }
