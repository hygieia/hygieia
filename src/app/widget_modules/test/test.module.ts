import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { TestConfigFormComponent } from './test-config-form/test-config-form.component';
import { TestDetailComponent } from './test-detail/test-detail.component';
import { TestWidgetComponent } from './test-widget/test-widget.component';
import {TestDeleteFormComponent} from './test-delete-form/test-delete-form.component';
import {NgbAccordionModule, NgbTypeaheadModule} from '@ng-bootstrap/ng-bootstrap';

@NgModule({
  declarations: [TestConfigFormComponent, TestDetailComponent, TestWidgetComponent, TestDeleteFormComponent],
  imports: [
    SharedModule,
    NgbTypeaheadModule,
    NgbAccordionModule
  ],
  entryComponents: [TestWidgetComponent, TestDetailComponent, TestConfigFormComponent, TestDeleteFormComponent]
})
export class TestModule { }
