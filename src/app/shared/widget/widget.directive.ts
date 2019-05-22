import {Directive, ViewContainerRef} from '@angular/core';

@Directive({
  selector: '[appWidget]'
})
export class WidgetDirective {

  constructor(public viewContainerRef: ViewContainerRef) { }


}
