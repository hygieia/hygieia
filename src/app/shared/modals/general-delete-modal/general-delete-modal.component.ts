import { Component, OnInit, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-general-delete-modal',
  templateUrl: './general-delete-modal.component.html',
  styleUrls: ['./general-delete-modal.component.scss']
})
export class GeneralDeleteComponent implements OnInit {

  @Input() public title;
  public message;
  public messageSubtext = 'This item will be deleted immediately, cannot undo the action';
  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit() {
    this.message = `Are you sure you want to delete this item : ${this.title} ?`;
  }

}
