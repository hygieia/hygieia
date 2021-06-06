import {Component, Input, OnInit, Type} from '@angular/core';
import {IClickListItemInfra} from '../../../shared/charts/click-list/click-list-interfaces';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-infra-scan-detail',
  templateUrl: './infra-scan-detail.component.html',
  styleUrls: ['./infra-scan-detail.component.sass']
})
export class InfraScanDetailComponent implements OnInit {

  @Input() detailView: Type<any>;
  public data: IClickListItemInfra;
  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit() {
  }

  @Input()
  set detailData(data: any) {
    this.data = data;
  }

  getData() {
    if (this.data && this.data.vulnerability) {
      return JSON.stringify(this.data.vulnerability, undefined, 1);
    }
  }
}
