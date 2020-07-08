import { Component, OnInit, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-general-delete',
  templateUrl: './general-delete.component.html',
  styleUrls: ['./general-delete.component.scss']
})
export class GeneralDeleteComponent implements OnInit {

  @Input() public title = 'DeleteModal';
  public confirm = 'Confirm';
  // public cancel = 'Cancel';
  public message = 'This item will be deleted immediately.  You cannot undo this action.';
  constructor(public activeModal: NgbActiveModal) { }


  ngOnInit() {
  }

}
