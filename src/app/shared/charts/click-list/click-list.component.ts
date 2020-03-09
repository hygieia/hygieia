import { Component, ViewEncapsulation } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DashStatus } from '../../dash-status/DashStatus';
import { DetailModalComponent } from '../../modals/detail-modal/detail-modal.component';
import { ChartComponent } from '../chart/chart.component';
import {IClickListData, IClickListItemDeploy} from './click-list-interfaces';
import {DeployDetailComponent} from '../../../widget_modules/deploy/deploy-detail/deploy-detail.component';

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

  openDetailView(clickListItem: IClickListItemDeploy) {
    // tslint:disable-next-line:max-line-length
    if (this.data && (this.data as IClickListData).clickableContent && clickListItem !== undefined && clickListItem != null && this.data !== undefined) {
      const currentDeploy = this.data.items.find(item => item.title === clickListItem.title);
      const modalRef = this.modalService.open(DeployDetailComponent);
      if (modalRef !== undefined && modalRef != null) {
        modalRef.componentInstance.title = currentDeploy.title;
        modalRef.componentInstance.name = currentDeploy.name;
        modalRef.componentInstance.lastUpdated = currentDeploy.lastUpdated;
        modalRef.componentInstance.version = currentDeploy.version;
        modalRef.componentInstance.url = currentDeploy.url;
        if (modalRef.componentInstance.url !== undefined) {
          modalRef.componentInstance.regex = modalRef.componentInstance.url.match(new RegExp('^(https?:\/\/)?(?:www\.)?([^\/]+)'))[0];
        }
        (modalRef.componentInstance as DeployDetailComponent).detailView = this.data.clickableContent;
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

