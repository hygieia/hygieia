import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BuildWidgetComponent } from './build-widget/build-widget.component';
import { BuildRoutingModule } from './build-routing-module';
import { SharedModule } from 'src/app/shared/shared.module';
import { TestFormComponent } from './test-form/test-form.component';
import {FormsModule} from '@angular/forms';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';



@NgModule({
    declarations: [BuildWidgetComponent],
    imports: [
        CommonModule,
        BuildRoutingModule,
        SharedModule,
        FormsModule
    ],
    exports: [
        BuildWidgetComponent
    ]
})
export class BuildModule { }
