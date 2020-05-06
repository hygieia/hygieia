import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TabComponent } from './tab.component';
import { TabsComponent } from './tabs.component';
import { IfTabActiveDirective } from './if-tab-active.directive';

@NgModule({
  declarations: [TabComponent, TabsComponent, IfTabActiveDirective],
  exports: [TabComponent, TabsComponent, IfTabActiveDirective],
  imports: [CommonModule]
})
export class TabsModule {}
