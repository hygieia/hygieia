import {Component, ViewEncapsulation} from '@angular/core';
import { ChartComponent } from '../chart/chart.component';
import { DashStatus } from '../../dash-status/DashStatus';
import {IClickListData, IClickListItem} from '../click-list/click-list-interfaces';
import {DetailModalComponent} from '../../modals/detail-modal/detail-modal.component';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-rotation-chart',
  templateUrl: './rotation-chart.component.html',
  styleUrls: ['./rotation-chart.component.scss'],
  encapsulation: ViewEncapsulation.None
})

export class RotationChartComponent extends ChartComponent {
  DashStatus: typeof DashStatus = DashStatus;

  constructor(private modalService: NgbModal) {
    super();
  }

  openDetailView(item: IClickListItem) {
    if (this.data && (this.data as IClickListData).clickableContent) {
      const modalRef = this.modalService.open(DetailModalComponent);
      modalRef.componentInstance.title = (item && item.title) ? item.title : 'Details';
      modalRef.componentInstance.detailData = item;
      (modalRef.componentInstance as DetailModalComponent).detailView = this.data.clickableContent;
    }
  }

  unlockTabs(targetAgileType: string, currAgileType: string) {
    if ((targetAgileType === 'scrumkanban') || (targetAgileType === 'scrum' && currAgileType === 'Scrum' ||
      (targetAgileType === 'kanban' && currAgileType === 'Kanban'))) {
      return true;
    } else {
      return false;
    }
  }
}
