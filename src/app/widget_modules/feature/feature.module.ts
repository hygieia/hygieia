import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import {FeatureWidgetComponent} from './feature-widget/feature-widget.component';
import {FeatureConfigFormComponent} from './feature-config-form/feature-config-form.component';
import {FeatureDetailComponent} from './feature-detail/feature-detail.component';
import {FeatureDeleteFormComponent} from './feature-delete-form/feature-delete-form.component';
import {NgbTypeaheadModule} from '@ng-bootstrap/ng-bootstrap';

@NgModule({
  declarations: [FeatureWidgetComponent, FeatureConfigFormComponent, FeatureDetailComponent, FeatureDeleteFormComponent],
  entryComponents: [FeatureWidgetComponent, FeatureConfigFormComponent, FeatureDetailComponent, FeatureDeleteFormComponent],
  imports: [
    SharedModule,
    NgbTypeaheadModule
  ]
})
export class FeatureModule { }
