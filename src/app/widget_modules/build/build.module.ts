import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SharedModule } from 'src/app/shared/shared.module';

import { BuildRoutingModule } from './build-routing-module';
import { BuildWidgetComponent } from './build-widget/build-widget.component';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    BuildRoutingModule,
    SharedModule
  ],
  exports: [

  ]
})
export class BuildModule { }
