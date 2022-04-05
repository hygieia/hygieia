import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { NfrrRoutingModule } from './nfrr-routing.module';
import {NgxChartsModule} from '@swimlane/ngx-charts';
import { NfrrViewComponent } from './nfrr-view/nfrr-view.component';

@NgModule({
  declarations: [NfrrViewComponent],
  imports: [
    CommonModule,
    NfrrRoutingModule,
    NgxChartsModule,
  ],
  exports: [],
})
export class NfrrModule { }
