import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';

@NgModule({
  declarations: [],
  imports: [
    SharedModule,
    CommonModule,
  ],
    exports: [CommonModule]
})
export class ProductDashboardModule { }
