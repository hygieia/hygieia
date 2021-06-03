import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RepoWidgetComponent} from './repo-widget/repo-widget.component';

const routes: Routes = [
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RepoRoutingModule {
  static components = [RepoWidgetComponent];
}
