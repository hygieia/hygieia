import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {TabsFixtureComponent} from './tabs.fixture';
import {TabsLabeltemplateFixtureComponent} from './tabs-label-template.fixture';
import {TabsMultipleActiveFixtureComponent} from './tabs-multiple-active.fixture';
import {TabsModule} from '../tabs.module';

@NgModule({
  declarations: [TabsFixtureComponent, TabsLabeltemplateFixtureComponent, TabsMultipleActiveFixtureComponent],
  imports: [
    CommonModule,
    TabsModule,
  ],
  exports: [TabsFixtureComponent, TabsLabeltemplateFixtureComponent, TabsMultipleActiveFixtureComponent]
})
export class TabsFixturesModule { }
