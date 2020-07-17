import { Component, OnInit, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-general-dashboard-delete',
  templateUrl: './general-dashboard-delete.component.html',
  styleUrls: ['./general-dashboard-delete.component.scss']
})
export class GeneralDashboardDeleteComponent implements OnInit {

  @Input() public title = 'DeleteModal';
  public confirm = 'Confirm';
  // public cancel = 'Cancel';
  public message = 'This item will be deleted immediately.  You cannot undo this action.';
  constructor(public activeModal: NgbActiveModal) { }


  ngOnInit() {
  }

}
