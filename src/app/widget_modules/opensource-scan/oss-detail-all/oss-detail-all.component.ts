import {Component, Input, OnInit, Type} from '@angular/core';
import {IClickListItemOSS} from '../../../shared/charts/click-list/click-list-interfaces';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-oss-detail-all',
  templateUrl: './oss-detail-all.component.html',
  styleUrls: ['./oss-detail-all.component.scss']
})
export class OSSDetailAllComponent implements OnInit {
  @Input() detailView: Type<any>;

  public data: IClickListItemOSS;

  constructor(
    public activeModal: NgbActiveModal,
  ) { }

  ngOnInit() {
  }

  @Input()
  set detailData(data: any) {
    if (data) {
      this.data = data.items;
    } else {
      this.data = null;
    }
  }
}
