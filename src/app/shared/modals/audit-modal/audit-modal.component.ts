import {Component, Input, OnInit} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {IAuditResult} from '../../interfaces';

@Component({
  selector: 'app-audit-modal',
  templateUrl: './audit-modal.component.html',
  styleUrls: ['./audit-modal.component.sass']
})
export class AuditModalComponent implements OnInit {

  constructor(public activeModal: NgbActiveModal) { }
  auditResultsArr: IAuditResult[];


  ngOnInit() {
  }

  @Input()
  set auditResults(auditResults) {
    this.auditResultsArr = auditResults;
  }

  toDate(timestamp: number) {
    return new Date(timestamp);
  }
}
