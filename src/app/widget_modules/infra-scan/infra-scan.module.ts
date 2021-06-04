import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InfraScanConfigComponent } from './infra-scan-config/infra-scan-config.component';
import { InfraScanWidgetComponent } from './infra-scan-widget/infra-scan-widget.component';
import { InfraScanDeleteComponent } from './infra-scan-delete/infra-scan-delete.component';
import {ReactiveFormsModule} from '@angular/forms';
import {SharedModule} from '../../shared/shared.module';
import {NgbTypeaheadModule} from '@ng-bootstrap/ng-bootstrap';
import { InfraScanDetailComponent } from './infra-scan-detail/infra-scan-detail.component';

@NgModule({
  declarations: [InfraScanConfigComponent, InfraScanWidgetComponent, InfraScanDeleteComponent, InfraScanDetailComponent],
  entryComponents: [InfraScanConfigComponent, InfraScanWidgetComponent, InfraScanDeleteComponent, InfraScanDetailComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    SharedModule,
    NgbTypeaheadModule,
  ],
  exports: [InfraScanWidgetComponent, InfraScanConfigComponent, InfraScanDeleteComponent]
})
export class InfraScanModule {}
