import { Component, ViewChild, ChangeDetectionStrategy } from '@angular/core';
import { TabsComponent } from '../tabs.component';

@Component({
  // tslint:disable-next-line: component-selector
  selector: 'tabs-label-template-fixture',
  templateUrl: 'tabs-label-template.fixture.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TabsLabeltemplateFixtureComponent {
  @ViewChild('tabs', { static: true }) tabsComponent: TabsComponent;
}
