import { Component, ViewEncapsulation } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DashStatus } from '../../dash-status/DashStatus';
import { DetailModalComponent } from '../../modals/detail-modal/detail-modal.component';
import { ChartComponent } from '../chart/chart.component';
import { IClickListData, IClickListItem } from './click-list-interfaces';

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
    if (this.data && (this.data as IClickListData).clickableContent && item !== undefined && this.data.obj !== undefined) {
      let pos = 0;
      this.data.obj.title.forEach((currTitle, i) => {
        if (currTitle === item.title) {
          pos = i;
        }
      });

      const modalRef = this.modalService.open(DetailModalComponent);
      if (modalRef !== undefined) {
        modalRef.componentInstance.title = this.data.obj.title[pos];
        modalRef.componentInstance.name = this.data.obj.name[pos];
        modalRef.componentInstance.lastUpdated = this.data.obj.lastUpdated[pos].match(new RegExp('[^ ]* (.*)(.*?)GMT'))[1];
        modalRef.componentInstance.version = this.data.obj.version[pos];
        modalRef.componentInstance.url = this.data.obj.url[pos];
        if (modalRef.componentInstance.url !== undefined) {
          modalRef.componentInstance.regex = modalRef.componentInstance.url.match(new RegExp('^(https?:\/\/)?(?:www\.)?([^\/]+)'))[0];
        }
        (modalRef.componentInstance as DetailModalComponent).detailView = this.data.clickableContent;
      }
    }
  }

  openHeaderView() {
    if (this.data && (this.data as IClickListData).clickableHeader) {
      const modalRef = this.modalService.open(DetailModalComponent);
      modalRef.componentInstance.title = 'Details';
      (modalRef.componentInstance as DetailModalComponent).detailView = this.data.clickableContent;
    }
  }

  isDate(obj): boolean {
    return obj instanceof Date;
  }
}

