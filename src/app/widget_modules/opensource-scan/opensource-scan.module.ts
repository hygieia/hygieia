import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { OSSWidgetComponent } from './oss-widget/oss-widget.component';
import { OSSDetailComponent } from './oss-detail/oss-detail.component';
import { OSSConfigFormComponent } from './oss-config-form/oss-config-form.component';
import {CommonModule} from '@angular/common';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { OSSDetailAllComponent } from './oss-detail-all/oss-detail-all.component';

@NgModule({
  declarations: [OSSWidgetComponent, OSSDetailComponent, OSSConfigFormComponent, OSSDetailAllComponent],
  entryComponents: [OSSDetailComponent, OSSConfigFormComponent],
  imports: [
    CommonModule,
    NgbModule,
    SharedModule
  ],
})
export class OpensourceScanModule { }
