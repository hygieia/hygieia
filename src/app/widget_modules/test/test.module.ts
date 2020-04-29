import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { TestConfigFormComponent } from './test-config-form/test-config-form.component';
import { TestDetailComponent } from './test-detail/test-detail.component';
import { TestWidgetComponent } from './test-widget/test-widget.component';

@NgModule({
  declarations: [TestConfigFormComponent, TestDetailComponent, TestWidgetComponent],
  imports: [
    SharedModule
  ]
})
export class TestModule { }
