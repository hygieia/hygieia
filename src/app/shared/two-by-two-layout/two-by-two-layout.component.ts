import {
    Component, ComponentFactoryResolver, ChangeDetectorRef, AfterViewInit, ViewChildren, QueryList
} from '@angular/core';
import { LayoutComponent } from '../layout/layout.component';
import { ChartDirective } from '../chart.directive';

@Component({
    selector: 'app-two-by-two-layout',
    templateUrl: './two-by-two-layout.component.html',
    styleUrls: ['./two-by-two-layout.component.scss']
})
export class TwoByTwoLayoutComponent extends LayoutComponent implements AfterViewInit {

    @ViewChildren(ChartDirective) childChartTags: QueryList<ChartDirective>;

    constructor(componentFactoryResolver: ComponentFactoryResolver, cdr: ChangeDetectorRef) {
        super(componentFactoryResolver, cdr);
    }

    ngAfterViewInit() {
        super.loadComponent(this.childChartTags);
    }

}
