import { Component, Input, OnInit } from '@angular/core';
import { NgbModal, NgbActiveModal } from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-configure-modal',
  templateUrl: './configure-modal.component.html',
  styleUrls: ['./configure-modal.component.scss']
})
export class ConfigureModalComponent implements OnInit {

  @Input() title = 'Test';

  constructor(
    public activeModal: NgbActiveModal
  ) { }

  ngOnInit() {

  }

}
