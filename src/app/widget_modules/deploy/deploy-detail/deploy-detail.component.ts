import {ChangeDetectorRef, Component, ComponentFactoryResolver, Input, OnInit, Type} from '@angular/core';
import {DetailModalComponent} from '../../../shared/modals/detail-modal/detail-modal.component';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-deploy-detail',
  templateUrl: './deploy-detail.component.html',
  styleUrls: ['./deploy-detail.component.scss']
})
export class DeployDetailComponent implements OnInit {
  @Input() detailView: Type<any>;

  constructor(
    public activeModal: NgbActiveModal,
  ) { }

  ngOnInit() {
  }

  closeModal() {
    if (this.activeModal) {
      this.activeModal.close('Modal Closed');
    }
  }
}
