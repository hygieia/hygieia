import { Component, OnInit, ComponentFactoryResolver, ChangeDetectorRef, ViewChild, AfterViewInit } from '@angular/core';
import { WidgetComponent } from 'src/app/shared/widget/widget.component';
import { TwoByTwoLayoutComponent } from 'src/app/shared/layouts/two-by-two-layout/two-by-two-layout.component';
import { LineChartComponent } from 'src/app/shared/charts/line-chart/line-chart.component';
import { LayoutDirective } from 'src/app/shared/layouts/layout.directive';
import { BuildService } from '../build.service';
import { Build } from '../interfaces';

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

    ngOnInit() {
        this.layout = TwoByTwoLayoutComponent;
        this.charts = [
            {
                component: LineChartComponent,
                data: [
                    {
                        name: 'All Builds',
                        series: []
                    },
                    {
                        name: 'Failed Builds',
                        series: []
                    }
                ],
                xAxisLabel: 'Days',
                yAxisLabel: 'Builds',
                colorScheme: {
                    domain: ['green', 'red']
                }
            }
        ];
    }

    ngAfterViewInit() {
        const fourteenDays = this.toMidnight(new Date());
        fourteenDays.setDate(fourteenDays.getDate() - 13);
        this.buildService.fetchDetails().subscribe(result => {
            console.log(result);
            const allBuilds = result.filter(build => this.checkBuildAfterDate(build, fourteenDays)
                && !this.checkBuildStatus(build, 'InProgress'));
            const failedBuilds = result.filter(build => this.checkBuildAfterDate(build, fourteenDays)
                && !this.checkBuildStatus(build, 'InProgress') && !this.checkBuildStatus(build, 'Success'));
            this.charts[0].data[0].series = this.countBuilds(allBuilds, fourteenDays);
            this.charts[0].data[1].series = this.countBuilds(failedBuilds, fourteenDays);
            console.log(this.charts);
            super.loadComponent(this.childLayoutTag);
        });

    }

    private toMidnight(date: Date): Date {
        date.setHours(0, 0, 0, 0);
        return date;
    }

    private checkBuildAfterDate(build: Build, date: Date): boolean {
        return build.endTime >= date.getTime();
    }

    private checkBuildStatus(build: Build, status: string): boolean {
        return build.buildStatus === status;
    }

    private countBuilds(builds: Build[], startDate: Date): any[] {
        const counts = {};
        const date = new Date(startDate.getTime());
        for (let i = 0; i < 14; i++) {
            counts[this.toMidnight(date).getTime()] = 0;
            date.setDate(date.getDate() + 1);
        }
        builds.forEach(build => {
            const index = this.toMidnight(new Date(build.endTime)).getTime();
            counts[index] = counts[index] + 1;
        });
        const dataArray = [];
        for (const key of Object.keys(counts)) {
            const data = counts[key];
            dataArray.push(
                {
                    name: new Date(+key),
                    value: data
                }
            );
        }
        return dataArray;

    }
}


