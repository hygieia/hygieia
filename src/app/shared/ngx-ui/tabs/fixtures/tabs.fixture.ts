import { Component, ViewChild, ChangeDetectionStrategy } from '@angular/core';
import { TabsComponent } from '../tabs.component';

@Component({
  // tslint:disable-next-line: component-selector
  selector: 'tabs-fixture',
  templateUrl: 'tabs.fixture.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TabsFixtureComponent {
  @ViewChild('tabs', { static: true }) tabsComponent: TabsComponent;
}
