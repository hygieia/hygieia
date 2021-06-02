import {Component, OnInit} from '@angular/core';
import {ICollItem} from '../../interfaces';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-collector-item-details',
  templateUrl: './collector-item-details.component.html',
  styleUrls: ['./collector-item-details.component.scss']
})
export class CollectorItemDetailsComponent implements OnInit {

  collectorDetails: ICollItem;
  collector: string;

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit() {
  }

  getJsonHtml( obj ) {
    return JSON.stringify(obj, undefined, 4);
  }
}
