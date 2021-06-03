import {ChangeDetectorRef, Component, ComponentFactoryResolver, Input, OnInit, Type, ViewChild} from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import {DeleteConfirmModalDirective} from './delete-confirm-modal.directive';

@Component({
  selector: 'app-delete-confirm-modal',
  templateUrl: './delete-confirm-modal.component.html',
  styleUrls: ['./delete-confirm-modal.component.scss']
})
export class DeleteConfirmModalComponent implements OnInit {

  @Input() public title: string;
  @Input() form: Type<any>;
  @Input() widgetConfig: Type<any>;
  @ViewChild(DeleteConfirmModalDirective, { static: true }) modalTypeTag: DeleteConfirmModalDirective;

  constructor(
    public activeModal: NgbActiveModal,
    private componentFactoryResolver: ComponentFactoryResolver,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    const componentFactory = this.componentFactoryResolver.resolveComponentFactory(this.form);
    const viewContainerRef = this.modalTypeTag.viewContainerRef;
    viewContainerRef.clear();
    const componentRef = viewContainerRef.createComponent(componentFactory);
    (componentRef.instance as DeleteConfirmModalComponent).widgetConfig = this.widgetConfig;
    this.cdr.detectChanges();
  }
}
