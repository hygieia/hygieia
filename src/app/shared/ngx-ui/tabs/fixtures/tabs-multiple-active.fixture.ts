import { Component, ViewChild, ChangeDetectionStrategy } from '@angular/core';
import { TabsComponent } from '../tabs.component';

@Component({
  // tslint:disable-next-line: component-selector
  selector: 'tabs-multiple-active-fixture',
  templateUrl: 'tabs-multiple-active.fixture.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TabsMultipleActiveFixtureComponent {
  @ViewChild('tabs', { static: true }) tabsComponent: TabsComponent;
}
