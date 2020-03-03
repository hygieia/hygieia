import {Component, NgModule, Pipe} from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { TimeAgoPipe } from 'time-ago-pipe';
import { DashStatusComponent } from '../../dash-status/dash-status.component';
import { DetailModalComponent } from '../../modals/detail-modal/detail-modal.component';
import { ClickListComponent } from './click-list.component';
import {CommonModule } from '@angular/common';
import {DashStatus, IClickListData, IClickListItem} from './click-list-interfaces';

@Component({
  selector: 'app-test-detail-view',
  template: '',
})
export class TestDetailViewComponent {}

@NgModule({
  declarations: [TestDetailViewComponent, DetailModalComponent],
  imports: [CommonModule],
  providers: [],
  entryComponents: [TestDetailViewComponent, DetailModalComponent]
})
class TestModule { }

describe('ClickListComponent', () => {
  let component: ClickListComponent;
  let fixture: ComponentFixture<ClickListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ClickListComponent, TimeAgoPipe, DashStatusComponent],
      imports: [TestModule, NgbModule ]
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
          url: 'testurl.com',
          lastUpdated: 12345
        } as IClickListItem
      ],
      clickableContent: TestDetailViewComponent,
      clickableHeader: TestDetailViewComponent
    } as IClickListData;
    component.openDetailView(null);
    component.openHeaderView();
  });
});
