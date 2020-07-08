import { Component, OnInit } from '@angular/core';
import {ICollItem} from '../../interfaces';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-collector-refresh',
  templateUrl: './collector-refresh.component.html',
  styleUrls: ['./collector-refresh.component.scss']
})
export class CollectorRefreshComponent implements OnInit {

  collectorDetails: ICollItem;

  constructor(public activeModal: NgbActiveModal) { }


  ngOnInit() {
  }

  getJsonHtml( obj ) {
    return JSON.stringify(obj, undefined, 4);
  }
}
