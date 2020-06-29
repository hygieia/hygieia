import {Component, NgModule, ViewRef} from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { TimeAgoPipe } from 'time-ago-pipe';
import { DashStatusComponent } from '../../dash-status/dash-status.component';
import { DetailModalComponent } from '../../modals/detail-modal/detail-modal.component';
import { ClickListComponent } from './click-list.component';
import {CommonModule } from '@angular/common';
import {DashStatus, IClickListData, IClickListItem} from './click-list-interfaces';
import {DeployDetailComponent} from '../../../widget_modules/deploy/deploy-detail/deploy-detail.component';

@Component({
  selector: 'app-test-detail-view',
  template: '',
})
export class TestDetailViewComponent {}

@NgModule({
  declarations: [TestDetailViewComponent, DetailModalComponent, DeployDetailComponent],
  imports: [CommonModule],
  providers: [],
  entryComponents: [TestDetailViewComponent, DetailModalComponent, DeployDetailComponent],
})
class TestModule { }

describe('ClickListComponent', () => {
  let component: ClickListComponent;
  let fixture: ComponentFixture<ClickListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ClickListComponent, TimeAgoPipe, DashStatusComponent],
      imports: [TestModule, NgbModule ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClickListComponent);
    component = fixture.componentInstance;
    if (!(fixture.changeDetectorRef as ViewRef).destroyed) {
      fixture.detectChanges();
    }
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should open detail view and header view', () => {
    component.data = {
      items: [
        {
          status: DashStatus.PASS,
          statusText: 'Passing',
          title: 'Test title',
          subtitles: [
            'Test'
          ],
          url: 'firstTestUrl.com',
          lastUpdated: 11111
        } as IClickListItem,
      ],
      clickableContent: TestDetailViewComponent,
      clickableHeader: TestDetailViewComponent
    } as IClickListData;

    component.openDetailView(null);
    component.openHeaderView(null, null);
  });

  it('should open detail view and set values', () => {
    component.data = {
      items: [
        {
          status: DashStatus.PASS,
          statusText: 'Passing',
          title: 'firstTest',
          subtitles: [
            'Test'
          ],
          url: 'firstTestUrl.com',
          lastUpdated: 12345
        } as IClickListItem,
        {
          status: DashStatus.PASS,
          statusText: 'Passing',
          title: 'secondTest',
          subtitles: [
            'Test'
          ],
          url: 'secondTestUrl.com',
          lastUpdated: 54321
        } as IClickListItem,
      ],
      clickableContent: TestDetailViewComponent,
      clickableHeader: TestDetailViewComponent
    } as IClickListData;

    component.data.items.title = 'secondTest';
    component.openDetailView(component.data.items);
  });
});
