import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TabsComponent } from './tabs.component';
import { TabsFixtureComponent } from './fixtures/tabs.fixture';
import { TabsLabeltemplateFixtureComponent } from './fixtures/tabs-label-template.fixture';
import { TabsModule } from './tabs.module';
import { TabsMultipleActiveFixtureComponent } from './fixtures/tabs-multiple-active.fixture';

describe('TabsComponent', () => {
  let component: TabsComponent;
  let fixture: ComponentFixture<TabsFixtureComponent>;
  describe('Standard Tabs', () => {
    beforeEach(done => {
      TestBed.configureTestingModule({
        declarations: [TabsFixtureComponent],
        imports: [TabsModule]
      }).compileComponents();

      fixture = TestBed.createComponent(TabsFixtureComponent);
      component = fixture.componentInstance.tabsComponent;
      fixture.autoDetectChanges();
      fixture.whenStable().then(() => {
        done();
      });
    });

    it('can load instance', () => {
      expect(component).toBeTruthy();
    });

    it('begins with the first tab set to active by default', () => {
      expect(component.index).toBe(0);
    });

    it('select defaults to: selectTab', () => {
      expect(component.select).toEqual(component.selectTab);
    });

    describe('next', () => {
      it('makes expected calls', () => {
        spyOn(component, 'move').and.callThrough();
        component.next();
        fixture.detectChanges();
        expect(component.move).toHaveBeenCalled();
        expect(component.index).toBe(1);
      });
    });

    describe('prev', () => {
      it('makes expected calls', () => {
        spyOn(component, 'move');
        component.prev();
        expect(component.move).toHaveBeenCalled();
      });
    });

    describe('move', () => {
      it('can move to specific tab', () => {
        component.move(3);
        expect(component.index).toBe(3);
      });

      it('cannot move to a disabled tab', () => {
        // tab at index 2 is disabled
        component.move(2);
        expect(component.index).not.toBe(2);
      });
    });
  });

  describe('Tabs with specific tab set active', () => {
    beforeEach(done => {
      TestBed.configureTestingModule({
        declarations: [TabsLabeltemplateFixtureComponent],
        imports: [TabsModule]
      }).compileComponents();

      fixture = TestBed.createComponent(TabsLabeltemplateFixtureComponent);
      component = fixture.componentInstance.tabsComponent;
      fixture.autoDetectChanges();
      fixture.whenStable().then(() => {
        done();
      });
    });

    it('4th tab is set to active on init', () => {
      expect(component.index).toBe(3);
    });

    it('renders template content for tab label correctly', () => {
      const el = document.getElementsByClassName('app-tab active')[0];
      expect(el.textContent).toContain('templated label');
    });
  });

  describe('Tabs with multiple active', () => {
    beforeEach(done => {
      TestBed.configureTestingModule({
        declarations: [TabsMultipleActiveFixtureComponent],
        imports: [TabsModule]
      }).compileComponents();

      spyOn(console, 'error');
      fixture = TestBed.createComponent(TabsMultipleActiveFixtureComponent);
      component = fixture.componentInstance.tabsComponent;
      fixture.autoDetectChanges();
      fixture.whenStable().then(() => {
        done();
      });
    });

    it('Tabs with multiple active tabs throws error when initialized', () => {
      expect(console.error).toHaveBeenCalled();
    });
  });
});
