import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BuildWidgetComponent } from './build-widget/build-widget.component';
import { BuildRoutingModule } from './build-routing-module';
import { SharedModule } from 'src/app/shared/shared.module';

@NgModule({
    declarations: [BuildWidgetComponent],
    imports: [
        CommonModule,
        BuildRoutingModule,
        SharedModule
    ],
    exports: [
        BuildWidgetComponent
    ]
})
export class BuildModule { }
