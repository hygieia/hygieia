import { Directive, ElementRef, ViewContainerRef } from '@angular/core';

@Directive({
  selector: '[appLayout]'
})
export class LayoutDirective {

  constructor(public viewContainerRef: ViewContainerRef, public el: ElementRef) { }

}
