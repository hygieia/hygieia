import { ChangeDetectorRef, Component, ComponentFactoryResolver, Input, Type } from '@angular/core';
import {LayoutComponent} from '../layouts/layout/layout.component';
import {LayoutDirective} from '../layouts/layout.directive';
import {Chart} from '../interfaces';
import {NgbModal, NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {DetailModalComponent} from '../modals/detail-modal/detail-modal.component';
import {FormModalComponent} from '../modals/form-modal/form-modal.component';
import {ConfirmationModalComponent} from '../modals/confirmation-modal/confirmation-modal.component';
import {TestFormComponent} from '../../widget_modules/build/test-form/test-form.component';


@Component({
  selector: 'app-widget',
  templateUrl: './widget.component.html',
  styleUrls: ['./widget.component.scss']
})
export class WidgetComponent {
  @Input() layout: Type<any>;

  public charts: Chart[];

  constructor(private componentFactoryResolver: ComponentFactoryResolver, private cdr: ChangeDetectorRef, private modalService: NgbModal) {
  }

  loadComponent(layoutTag: LayoutDirective) {
    const componentFactory = this.componentFactoryResolver.resolveComponentFactory(this.layout);
    const viewContainerRef = layoutTag.viewContainerRef;
    viewContainerRef.clear();
    const componentRef = viewContainerRef.createComponent(componentFactory);
    (componentRef.instance as LayoutComponent).charts = this.charts;
    this.cdr.detectChanges();
  }

  openConfig() {
    const modalRef = this.modalService.open(FormModalComponent);
    modalRef.componentInstance.title = 'Configure';
    modalRef.componentInstance.modalType = TestFormComponent;
    modalRef.componentInstance.id = 1;
    modalRef.result.then((result) => {
      console.log(result);
    }).catch((error) => {
      console.log(error);
    });
  }

  openConfirm() {
    const modalRef = this.modalService.open(ConfirmationModalComponent);
    modalRef.componentInstance.title = 'Are you sure want to delete this widget from your dashboard?';
    // modalRef.componentInstance.modalType = ConfirmationModalComponent;
  }
}


