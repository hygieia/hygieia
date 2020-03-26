import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { CommonModule } from '@angular/common';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RepoConfigFormComponent} from './repo-config-form/repo-config-form.component';
import { RepoWidgetComponent} from './repo-widget/repo-widget.component';
import { RepoDetailComponent} from './repo-detail/repo-detail.component';
import { RepoRoutingModule } from './repo-routing-module';

@NgModule({
  declarations: [RepoWidgetComponent, RepoConfigFormComponent, RepoDetailComponent],
  entryComponents: [RepoConfigFormComponent, RepoDetailComponent],
  imports: [
    RepoRoutingModule,
    CommonModule,
    NgbModule,
    SharedModule
  ],
  exports: []
})
export class RepoModule { }
