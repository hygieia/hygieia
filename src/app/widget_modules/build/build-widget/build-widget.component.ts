import { Component, OnInit, ComponentFactoryResolver, ChangeDetectorRef, ViewChild, AfterViewInit } from '@angular/core';
import { WidgetComponent } from 'src/app/shared/widget/widget.component';
import { TwoByTwoLayoutComponent } from 'src/app/shared/two-by-two-layout/two-by-two-layout.component';
import { LineChartComponent } from 'src/app/shared/line-chart/line-chart.component';
import { LayoutDirective } from 'src/app/shared/layout.directive';
import { BuildService } from '../build.service';

@Component({
    selector: 'app-build-widget',
    templateUrl: './build-widget.component.html',
    styleUrls: ['./build-widget.component.scss']
})
export class BuildWidgetComponent extends WidgetComponent implements OnInit, AfterViewInit {

    @ViewChild(LayoutDirective) childLayoutTag: LayoutDirective;

    constructor(componentFactoryResolver: ComponentFactoryResolver, cdr: ChangeDetectorRef, private buildService: BuildService) {
        super(componentFactoryResolver, cdr);
    }

    ngAfterViewInit() {
        super.loadComponent(this.childLayoutTag);
    }

    ngOnInit() {
        this.buildService.fetchDetails().subscribe(response =>
            console.log(response)
        );
        this.layout = TwoByTwoLayoutComponent;
        this.charts = [
            {
                component: LineChartComponent,
                data: [
                    {
                        name: 'Germany',
                        series: [
                            {
                                name: '2010',
                                value: 7300000
                            },
                            {
                                name: '2011',
                                value: 8940000
                            }
                        ]
                    }
                ]
            },
            {
                component: LineChartComponent,
                data: [
                    {
                        name: 'Germany',
                        series: [
                            {
                                name: '2010',
                                value: 7300000
                            },
                            {
                                name: '2011',
                                value: 8940000
                            }
                        ]
                    }
                ]
            },
            {
                component: LineChartComponent,
                data: [
                    {
                        name: 'Germany',
                        series: [
                            {
                                name: '2010',
                                value: 7300000
                            },
                            {
                                name: '2011',
                                value: 8940000
                            }
                        ]
                    }
                ]
            },
            {
                component: LineChartComponent,
                data: [
                    {
                        name: 'Germany',
                        series: [
                            {
                                name: '2010',
                                value: 7300000
                            },
                            {
                                name: '2011',
                                value: 8940000
                            }
                        ]
                    }
                ]
            }
        ];
    }
}


