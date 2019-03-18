import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
  selector: '[appModal]'
})
export class ModalDirective {

  constructor(public viewContainerRef: ViewContainerRef) { }

}
