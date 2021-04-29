import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SecurityScanRoutingModule } from './security-scan-routing.module';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../../shared/shared.module';
import { SecurityScanWidgetComponent } from './security-scan-widget/security-scan-widget.component';
import { SecurityScanConfigComponent } from './security-scan-config/security-scan-config.component';
import { SecurityScanDeleteFormComponent } from './security-scan-delete-form/security-scan-delete-form.component';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { SecurityScanDetailComponent } from './security-scan-detail/security-scan-detail.component';
import { MatIconModule } from '@angular/material/icon';
// import { RefreshModalComponent } from '../../shared/modals/refresh-modal/refresh-modal.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RefreshModalComponent } from 'src/app/shared/modals/refresh-modal/refresh-modal.component';



@NgModule({
  declarations: [SecurityScanWidgetComponent, SecurityScanConfigComponent, SecurityScanDeleteFormComponent,
                 SecurityScanDetailComponent],
  entryComponents: [SecurityScanWidgetComponent, SecurityScanConfigComponent, SecurityScanDeleteFormComponent,
                    SecurityScanDetailComponent, RefreshModalComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    SharedModule,
    SecurityScanRoutingModule,
    NgbTypeaheadModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  exports: [SecurityScanWidgetComponent, SecurityScanConfigComponent]
})
export class SecurityScanModule { }
