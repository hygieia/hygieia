import { Component, NgModule } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { TimeAgoPipe } from 'time-ago-pipe';

import { DashStatusComponent } from '../../dash-status/dash-status.component';
import { DashStatus } from '../../dash-status/DashStatus';
import { DetailModalComponent } from '../../modals/detail-modal/detail-modal.component';
import { IClickListData, IClickListItem } from './click-list-interfaces';
import { ClickListComponent } from './click-list.component';

@Component({
  selector: 'app-test-detail-view',
  template: '',
})
export class TestDetailViewComponent {}

@NgModule({
  declarations: [TestDetailViewComponent, DetailModalComponent],
  imports: [],
  entryComponents: [TestDetailViewComponent, DetailModalComponent]
})
class TestModule { }

describe('ClickListComponent', () => {
  let component: ClickListComponent;
  let fixture: ComponentFixture<ClickListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ClickListComponent, TimeAgoPipe, DashStatusComponent],
      imports: [TestModule, NgbModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClickListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should open detail views', () => {
    component.data = {
      items: [
        {
          status: DashStatus.PASS,
          statusText: 'Passing',
          title: 'Test',
          subtitles: [
            'Test'
          ],
        } as IClickListItem
      ],
      clickableContent: TestDetailViewComponent,
      clickableHeader: TestDetailViewComponent
    } as IClickListData;
    component.openDetailView(null);
    component.openHeaderView();
  });
});
