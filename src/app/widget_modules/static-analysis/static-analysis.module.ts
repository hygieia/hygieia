import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SharedModule } from 'src/app/shared/shared.module';

import { StaticAnalysisConfigFormComponent } from './static-anaylsis-config-form/static-analysis-config-form.component';
import { StaticAnalysisDetailComponent } from './static-analysis-detail/static-analysis-detail.component';
import { StaticAnalysisRoutingModule } from './static-analysis-routing-module';
import { StaticAnalysisWidgetComponent } from './static-analysis-widget/static-analysis-widget.component';

@NgModule({
  declarations: [StaticAnalysisWidgetComponent, StaticAnalysisConfigFormComponent, StaticAnalysisDetailComponent],
  entryComponents: [StaticAnalysisConfigFormComponent, StaticAnalysisDetailComponent],
  imports: [
    StaticAnalysisRoutingModule,
    CommonModule,
    NgbModule,
    SharedModule
  ],
  exports: [],
})
export class StaticAnalysisModule { }
