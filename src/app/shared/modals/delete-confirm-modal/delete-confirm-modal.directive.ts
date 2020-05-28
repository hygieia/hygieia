import {Directive, ViewContainerRef} from '@angular/core';

@Directive({
  selector: '[appDeleteConfirmModal]'
})
export class DeleteConfirmModalDirective {

  constructor(public viewContainerRef: ViewContainerRef) {
  }

}
