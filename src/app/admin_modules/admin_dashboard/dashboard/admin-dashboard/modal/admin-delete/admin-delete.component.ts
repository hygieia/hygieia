import { Component, OnInit, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-admin-delete',
  templateUrl: './admin-delete.component.html',
  styleUrls: ['./admin-delete.component.scss']
})
export class AdminDeleteComponent implements OnInit {

  @Input() public title = 'DeleteModal';
  public confirm = 'Confirm';
  // public cancel = 'Cancel';
  public message = 'This item will be deleted immediately.  You cannot undo this action.';
  constructor(public activeModal: NgbActiveModal) { }


  ngOnInit() {
  }

}
