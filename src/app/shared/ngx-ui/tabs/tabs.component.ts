import {
  Component,
  Input,
  Output,
  ContentChildren,
  QueryList,
  EventEmitter,
  ViewEncapsulation,
  AfterContentInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  OnDestroy, HostBinding
} from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { TabComponent } from './tab.component';

// This component is based on the tabs component from ngx-ui
// https://github.com/swimlane/ngx-ui/blob/master/projects/swimlane/ngx-ui/src/lib/components/tabs/tabs.component.ts
@Component({
  exportAs: 'appTabs',
  selector: 'app-tabs',
  templateUrl: './tabs.component.html',
  encapsulation: ViewEncapsulation.None,
  styleUrls: ['./tabs.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TabsComponent implements AfterContentInit, OnDestroy {
  @HostBinding('class') class = 'app-tabs';

  @Input() vertical: boolean;

  @Output() selectTab = new EventEmitter();
  // For backwards compat... user selectTab instead.
  @Output() select = this.selectTab;

  @ContentChildren(TabComponent) readonly tabs: QueryList<TabComponent>;

  get index(): number {
    const tabs = this.tabs.toArray();
    return tabs.findIndex(tab => tab.active);
  }

  private readonly destroy = new Subject<void>();

  constructor(readonly cdr: ChangeDetectorRef) {}

  ngAfterContentInit(): void {
    const tabs = this.tabs.toArray();
    const actives = this.tabs.filter(t => t.active);

    if (actives.length > 1) {
      console.error(`Multiple active tabs set 'active'`);
    } else if (!actives.length && tabs.length) {
      setTimeout(() => {
        tabs[0].active = true;
        tabs[0].detectChanges();
        this.cdr.markForCheck();
      });
    }

    this.tabs.changes.pipe(takeUntil(this.destroy)).subscribe(() => this.cdr.markForCheck());
  }

  ngOnDestroy() {
    this.destroy.next();
    this.destroy.complete();
  }

  tabClicked(activeTab: TabComponent): void {
    this.tabs.forEach(tab => (tab.active = false));

    activeTab.active = true;
    this.tabs.forEach(tab => tab.detectChanges());
    this.cdr.markForCheck();

    this.selectTab.emit(activeTab);
  }

  move(offset: number) {
    const tabs = this.tabs.toArray();
    for (let i = this.index + offset; i < tabs.length && i >= 0; i += offset) {
      const tab = tabs[i];
      if (tab && !tab.disabled) {
        this.tabClicked(tabs[i]);
        return;
      }
    }
  }

  next(): void {
    this.move(1);
  }

  prev(): void {
    this.move(-1);
  }
}
