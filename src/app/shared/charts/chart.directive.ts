import { Input, Directive, Type, ViewContainerRef } from '@angular/core';

@Directive({
    selector: '[appChart]'
})
export class ChartDirective {

    constructor(public viewContainerRef: ViewContainerRef) { }

}
