import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Component, Input, OnInit  } from '@angular/core';


@Component({
  selector: 'app-security-scan-refresh-modal',
  templateUrl: './security-scan-refresh-modal.component.html',
  styleUrls: ['./security-scan-refresh-modal.component.scss']
})
export class SecurityScanRefreshModalComponent implements OnInit {

  public detailData: any;
  @Input() title: any;
  @Input() message: string;

  constructor(
    public activeModal: NgbActiveModal,
  ) { }

  ngOnInit() {

  }
  onSubmit() {
    if (this.activeModal) {
      this.activeModal.close();
    }
  }
  closeModal() {
    if (this.activeModal) {
      this.activeModal.close();
    }
  }

}
