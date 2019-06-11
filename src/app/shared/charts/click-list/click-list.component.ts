import { Component, ViewEncapsulation } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { DetailModalComponent } from '../../modals/detail-modal/detail-modal.component';
import { ChartComponent } from '../chart/chart.component';
import { DashStatus, IClickListItem } from './click-list-interfaces';

@Component({
  selector: 'app-click-list',
  templateUrl: './click-list.component.html',
  styleUrls: ['./click-list.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ClickListComponent extends ChartComponent {
  DashStatus: typeof DashStatus = DashStatus;

  constructor(private modalService: NgbModal) {
    super();
  }


  openDetailView(item: IClickListItem) {
    if (this.data.clickableContent) {
      const modalRef = this.modalService.open(DetailModalComponent);
      modalRef.componentInstance.title = 'Details';
      if (this.data && this.data.clickableContent) {
        (modalRef.componentInstance as DetailModalComponent).detailView = this.data.clickableContent;
      }
    }
  }

  headerClicked() {
    console.log('Header clicked');
  }

  isDate(obj): boolean {
    return obj instanceof Date;
  }
}

