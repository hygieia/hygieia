import {Component, OnInit} from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
@Component({
  selector: 'app-view-json',
  templateUrl: './view-json.component.html',
  styleUrls: ['./view-json.component.scss']
})
export class ViewJsonComponent implements OnInit {
  constructor(public activeModal: NgbActiveModal) {}
  ngOnInit() {
  }
}
