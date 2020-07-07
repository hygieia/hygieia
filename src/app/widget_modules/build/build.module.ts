import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SharedModule } from 'src/app/shared/shared.module';

import { BuildConfigFormComponent } from './build-config-form/build-config-form.component';
import { BuildDetailComponent } from './build-detail/build-detail.component';
import { BuildRoutingModule } from './build-routing-module';
import { BuildWidgetComponent } from './build-widget/build-widget.component';
import {BuildDeleteFormComponent} from './build-delete-form/build-delete-form.component';

@NgModule({
  declarations: [BuildWidgetComponent, BuildConfigFormComponent, BuildDetailComponent, BuildDeleteFormComponent],
  entryComponents: [BuildWidgetComponent, BuildConfigFormComponent, BuildDetailComponent, BuildDeleteFormComponent],
  imports: [
    BuildRoutingModule,
    CommonModule,
    NgbModule,
    SharedModule,
  ],
  exports: []
})
export class BuildModule { }
