import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { ConfigureModalComponent } from './configure-modal/configure-modal.component';
import { ModalComponent } from './modal/modal.component';

@NgModule({
  declarations: [ConfigureModalComponent, ModalComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  exports: [
    CommonModule,
    ReactiveFormsModule
  ],
  entryComponents:[
    ModalComponent
  ]
})
export class SharedModule { }
