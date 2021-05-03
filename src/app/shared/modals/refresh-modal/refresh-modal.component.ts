import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Component, Input, OnInit  } from '@angular/core';


@Component({
  selector: 'app-refresh-modal',
  templateUrl: './refresh-modal.component.html',
  styleUrls: ['./refresh-modal.component.scss']
})
export class RefreshModalComponent implements OnInit {

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
