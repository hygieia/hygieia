import {Component, Input, OnInit, Type} from '@angular/core';
import {IClickListItemOSS} from '../../../shared/charts/click-list/click-list-interfaces';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-oss-detail',
  templateUrl: './oss-detail.component.html',
  styleUrls: ['./oss-detail.component.scss']
})
export class OSSDetailComponent implements OnInit {
  @Input() detailView: Type<any>;

  public data: IClickListItemOSS;

  constructor(
    public activeModal: NgbActiveModal,
  ) { }

  ngOnInit() {
  }

  @Input()
  set detailData(data: any) {
    this.data = data;
  }

  toDate(lastUpdated: number) {
    return new Date(lastUpdated).toDateString();
  }
}
