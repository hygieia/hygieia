import { CommonModule } from '@angular/common';
import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SharedModule } from 'src/app/shared/shared.module';

import { BuildConfigFormComponent } from './build-config-form/build-config-form.component';
import { BuildDetailComponent } from './build-detail/build-detail.component';
import { BuildRoutingModule } from './build-routing-module';
import { BuildWidgetComponent } from './build-widget/build-widget.component';
import {BuildDeleteFormComponent} from './build-delete-form/build-delete-form.component';
import { BuildDetailPageComponent } from './build-detail-page/build-detail-page.component';
import {MatStepperModule} from '@angular/material/stepper';
import {MatIconModule} from '@angular/material/icon';
import {MatTooltipModule} from '@angular/material/tooltip';
import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';
import { FlexLayoutModule } from '@angular/flex-layout';
import { BuildService } from './build.service';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from 'src/app/core/interceptors/auth.interceptor';
import { BuildViewerComponent } from './build-viewer/build-viewer.component';
import { FormsModule } from '@angular/forms';
import { NbInputModule } from '@nebular/theme';


@NgModule({
  providers: [
    {
      provide: STEPPER_GLOBAL_OPTIONS,
      useValue: { displayDefaultIndicatorType: false }
    },
    BuildService,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  declarations: [
    BuildWidgetComponent, BuildConfigFormComponent, BuildDetailComponent,
    BuildDeleteFormComponent, BuildDetailPageComponent, BuildViewerComponent
  ],
  entryComponents: [
    BuildWidgetComponent,
    BuildConfigFormComponent,
    BuildDetailComponent,
    BuildDeleteFormComponent,
    BuildDetailPageComponent,
    BuildViewerComponent
  ],
  imports: [
    BuildRoutingModule,
    CommonModule,
    NgbModule,
    SharedModule,
    MatStepperModule,
    MatIconModule,
    MatTooltipModule,
    FlexLayoutModule,
    FormsModule,
    NgbModule,
    NbInputModule
    ],
  exports: [],
  schemas: [
    CUSTOM_ELEMENTS_SCHEMA
  ]
})
export class BuildModule { }
