import {Directive, ElementRef, ViewContainerRef} from '@angular/core';

@Directive({
  selector: '[appWidget]'
})
export class WidgetDirective {

  constructor(public viewContainerRef: ViewContainerRef, public el: ElementRef) { }


}
