import { ClickListComponent } from 'src/app/shared/charts/click-list/click-list.component';
import { ComboChartComponent } from 'src/app/shared/charts/combo-chart/combo-chart.component';
import { LineChartComponent } from 'src/app/shared/charts/line-chart/line-chart.component';
import { NumberCardChartComponent } from 'src/app/shared/charts/number-card-chart/number-card-chart.component';
import { IChart } from 'src/app/shared/interfaces';

export let BUILD_CHARTS: IChart[] = [
  {
    title: 'Builds Per Day',
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
    title: 'Latest Builds',
    component: ClickListComponent,
    data: [],
    xAxisLabel: '',
    yAxisLabel: '',
    colorScheme: {}
  },
  {
    title: 'Average Build Duration',
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
    title: 'Total Builds',
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
