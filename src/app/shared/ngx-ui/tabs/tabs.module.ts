import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TabComponent } from './tab.component';
import { TabsComponent } from './tabs.component';
import { IfTabActiveDirective } from './if-tab-active.directive';
import {TabsLabeltemplateFixtureComponent} from './fixtures/tabs-label-template.fixture';
import {TabsMultipleActiveFixtureComponent} from './fixtures/tabs-multiple-active.fixture';
import {TabsFixtureComponent} from './fixtures/tabs.fixture';

@NgModule({
  declarations: [TabComponent, TabsComponent, IfTabActiveDirective,
    TabsLabeltemplateFixtureComponent, TabsMultipleActiveFixtureComponent, TabsFixtureComponent],
  exports: [TabComponent, TabsComponent, IfTabActiveDirective],
  imports: [CommonModule]
})
export class TabsModule {}
