import { Component, OnInit, ComponentFactoryResolver, ChangeDetectorRef, ViewChild, AfterViewInit } from '@angular/core';
import { WidgetComponent } from 'src/app/shared/widget/widget.component';
import { TwoByTwoLayoutComponent } from 'src/app/shared/layouts/two-by-two-layout/two-by-two-layout.component';
import { LineChartComponent } from 'src/app/shared/charts/line-chart/line-chart.component';
import { LayoutDirective } from 'src/app/shared/layouts/layout.directive';
import { BuildService } from '../build.service';
import { Build } from '../interfaces';
import { NumberCardChartComponent } from 'src/app/shared/charts/number-card-chart/number-card-chart.component';
import { ComboChartComponent } from 'src/app/shared/charts/combo-chart/combo-chart.component';
import { ClickListComponent } from 'src/app/shared/charts/click-list/click-list.component';

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
            },
            {
                component: ClickListComponent,
                data: [],
                xAxisLabel: '',
                yAxisLabel: '',
                colorScheme: {}
            },
            {
                component: ComboChartComponent,
                data: [
                    [],
                    [{
                        name: 'Threshold Line',
                        series: []
                    }]
                ],
                xAxisLabel: 'Days',
                yAxisLabel: 'Build Duration',
                colorScheme: {}
            },
            {
                component: NumberCardChartComponent,
                data: [
                    {
                        name: 'Today',
                        value: 0
                    },
                    {
                        name: 'Last 7 Days',
                        value: 0
                    },
                    {
                        name: 'Last 14 Days',
                        value: 0
                    }
                ],
                xAxisLabel: '',
                yAxisLabel: '',
                colorScheme: 'nightLights'
            },
        ];
    }

    ngAfterViewInit() {
        this.buildService.fetchDetails('59f88f5e6a3cf205f312c62e', 14).subscribe(result => {
            this.generateBuildsPerDay(result);
            this.generateTotalBuildCounts(result);
            this.generateAverageBuildDuration(result);
            this.generateLatestBuilds(result);
            super.loadComponent(this.childLayoutTag);
        });
    }

    // *********************** BUILDS PER DAY ****************************
    private generateBuildsPerDay(result: Build[]) {
        const fourteenDays = this.toMidnight(new Date());
        fourteenDays.setDate(fourteenDays.getDate() - 13);
        const allBuilds = result.filter(build => this.checkBuildAfterDate(build, fourteenDays)
            && !this.checkBuildStatus(build, 'InProgress'));
        const failedBuilds = result.filter(build => this.checkBuildAfterDate(build, fourteenDays)
            && !this.checkBuildStatus(build, 'InProgress') && !this.checkBuildStatus(build, 'Success'));
        this.charts[0].data[0].series = this.countBuildsPerDay(allBuilds, fourteenDays);
        this.charts[0].data[1].series = this.countBuildsPerDay(failedBuilds, fourteenDays);
    }

    private countBuildsPerDay(builds: Build[], startDate: Date): any[] {
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

    // *********************** LATEST BUILDS *****************************

    private generateLatestBuilds(result: Build[]) {
        const sorted = result.sort((a: Build, b: Build): number => {
            return a.endTime - b.endTime;
        }).reverse().slice(0, 5);
        const latestBuildData = sorted.map(build => {
            return {
                status: build.buildStatus.toLowerCase(),
                number: build.number,
                endTime: build.endTime,
                url: build.buildUrl
            };
        });
        this.charts[1].data = latestBuildData;
    }

    // *********************** TOTAL BUILD COUNTS ************************

    private generateTotalBuildCounts(result: Build[]) {
        const today = this.toMidnight(new Date());
        const sevenDays = this.toMidnight(new Date());
        const fourteenDays = this.toMidnight(new Date());
        sevenDays.setDate(sevenDays.getDate() - 6);
        fourteenDays.setDate(fourteenDays.getDate() - 13);

        const todayCount = result.filter(build => this.checkBuildAfterDate(build, today)).length;
        const sevenDayCount = result.filter(build => this.checkBuildAfterDate(build, sevenDays)).length;
        const fourteenDayCount = result.filter(build => this.checkBuildAfterDate(build, fourteenDays)).length;

        this.charts[3].data[0].value = todayCount;
        this.charts[3].data[1].value = sevenDayCount;
        this.charts[3].data[2].value = fourteenDayCount;
    }

    // *********************** AVERAGE BUILD DURATION *********************

    private generateAverageBuildDuration(result: Build[]) {
        const fourteenDays = this.toMidnight(new Date());
        const threshold = 900000;
        fourteenDays.setDate(fourteenDays.getDate() - 13);
        const successBuilds = result.filter(build => this.checkBuildAfterDate(build, fourteenDays)
            && this.checkBuildStatus(build, 'Success'));
        const averagedData = this.getAveragesByThreshold(successBuilds, fourteenDays, threshold);
        const thresholdLine = this.getConstantLineSeries(fourteenDays, threshold);
        this.charts[2].data[0] = averagedData.series;
        this.charts[2].colorScheme.domain = averagedData.colors;
        this.charts[2].data[1][0].series = thresholdLine;
    }

    private getAveragesByThreshold(builds: Build[], startDate: Date, threshold: number): any {
        const dataBucket = {};
        const date = new Date(startDate.getTime());
        for (let i = 0; i < 14; i++) {
            dataBucket[this.toMidnight(date).getTime()] = [];
            date.setDate(date.getDate() + 1);
        }

        // Group by build time
        builds.forEach(build => {
            const index = this.toMidnight(new Date(build.endTime)).getTime();
            dataBucket[index].push(build.duration);
        });

        return this.getAveragesSeries(dataBucket, threshold);
    }

    private getAveragesSeries(dataBucket: any, threshold: number): any {
        const series = [];
        const colors = [];
        for (const key of Object.keys(dataBucket)) {
            const data = dataBucket[key];
            let value = 0;
            if (data && data.length) {
                value = data.reduce((a: number, b: number) => {
                    return a + b;
                }) / data.length;
            }
            series.push(
                {
                    name: new Date(+key),
                    value
                }
            );
            if (value > threshold) {
                colors.push('red');
            } else {
                colors.push('green');
            }
        }
        return {series, colors};
    }

    private getConstantLineSeries(startDate: Date, threshold: number): any {
        const date = new Date(startDate.getTime());
        const series = [];
        for (let i = 0; i < 14; i++) {
            series.push({
                name: new Date(date.getTime()),
                value: threshold
            });
            date.setDate(date.getDate() + 1);
        }
        return series;
    }

    //// *********************** HELPER UTILS *********************


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
}


