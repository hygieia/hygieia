import { Component, Input, OnInit } from '@angular/core';
import { NgbModal, NgbActiveModal } from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-modal',
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.scss']
})
export class ModalComponent implements OnInit {

  @Input() title = 'Test';

  constructor(
    public activeModal: NgbActiveModal
  ) { }

  ngOnInit() {

  }

}
