import {Component, Input, OnInit} from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-delete-confirm-modal',
  templateUrl: './delete-confirm-modal.component.html',
  styleUrls: ['./delete-confirm-modal.component.scss']
})
export class DeleteConfirmModalComponent implements OnInit {

  @Input() public title: string;
  public confirm = 'Confirm';
  public cancel = 'Cancel';
  public message = 'This item will be deleted immediately.  You cannot undo this action.';
  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit() {
  }
}
