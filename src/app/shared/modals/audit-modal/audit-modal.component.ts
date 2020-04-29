import {Component, ComponentFactoryResolver, Input, OnInit} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-audit-modal',
  templateUrl: './audit-modal.component.html',
  styleUrls: ['./audit-modal.component.sass']
})
export class AuditModalComponent implements OnInit {

  constructor(public activeModal: NgbActiveModal) { }
  private type: string;
  private status: string;
  private reason: string;
  private lastAudited: number;
  private url: string;

  ngOnInit() {
  }

  @Input()
  set auditResult(auditResult) {
    if (auditResult) {
      this.type = auditResult.auditType;
      this.status = auditResult.auditStatus === 'NA' ?
        'COLLECTOR ' + auditResult.auditTypeStatus : auditResult.auditStatus;
      this.reason = auditResult.auditDetails;
      this.lastAudited = auditResult.timestamp;
      this.url = auditResult.url;
    }
  }
}
