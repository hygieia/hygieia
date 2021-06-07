import {IChart} from '../../../shared/interfaces';
import {ClickListComponent} from '../../../shared/charts/click-list/click-list.component';
import {NumberCardChartComponent} from '../../../shared/charts/number-card-chart/number-card-chart.component';

export let INFRA_SCAN_CHARTS: IChart[] =  [
  {
    title: 'Top 5 Vulnerabilities',
    component: ClickListComponent,
    data: [],
    xAxisLabel: '',
    yAxisLabel: '',
    colorScheme: {}
  },
  {
    title: 'Summary',
    component: NumberCardChartComponent,
    data: [
      {
        name: 'Critical',
        value: 0,
      },
      {
        name: 'High',
        value: 0,
      },
      {
        name: 'Medium',
        value: 0,
      },
      {
        name: 'Others',
        value: 0,
      }
    ],
    xAxisLabel: '',
    yAxisLabel: '',
    scaleFactor: 0.55,
    colorScheme: 'flame',
  }
];
