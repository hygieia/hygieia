import { Component } from '@angular/core';
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {ModalComponent} from "./shared/modals/modal/modal.component";
import {DetailModalComponent} from "./shared/modals/detail-modal/detail-modal.component";
import {FormModalComponent} from "./shared/modals/form-modal/form-modal.component";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'hygieia-ui';

  constructor(private modalService: NgbModal) {}

  open() {
    const modalRef = this.modalService.open(ModalComponent);
    modalRef.componentInstance.title = 'test';
    modalRef.componentInstance.modalType = FormModalComponent;
  }
}
