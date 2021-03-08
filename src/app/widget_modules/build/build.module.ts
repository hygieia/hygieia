import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SharedModule } from 'src/app/shared/shared.module';

import { BuildConfigFormComponent } from './build-config-form/build-config-form.component';
import { BuildDetailComponent } from './build-detail/build-detail.component';
import { BuildRoutingModule } from './build-routing-module';
import { BuildWidgetComponent } from './build-widget/build-widget.component';
import {BuildDeleteFormComponent} from './build-delete-form/build-delete-form.component';
import {MatStepperModule} from '@angular/material/stepper';
import {MatIconModule} from '@angular/material/icon';
import {MatTooltipModule} from '@angular/material/tooltip'
import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';

@NgModule({
  providers: [
    {
      provide: STEPPER_GLOBAL_OPTIONS,
      useValue: { displayDefaultIndicatorType: false }
    }
  ],
  declarations: [BuildWidgetComponent, BuildConfigFormComponent, BuildDetailComponent, BuildDeleteFormComponent],
  entryComponents: [BuildWidgetComponent, BuildConfigFormComponent, BuildDetailComponent, BuildDeleteFormComponent],
  imports: [
    BuildRoutingModule,
    CommonModule,
    NgbModule,
    SharedModule,
    MatStepperModule,
    MatIconModule,
    MatTooltipModule
    ],
  exports: []
})
export class BuildModule { }
