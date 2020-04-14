import { IChart } from 'src/app/shared/interfaces';

import {GaugeChartComponent} from '../../../shared/charts/gauge-chart/gauge-chart.component';
import {IGaugeChartData} from '../../../shared/charts/gauge-chart/gauge-chart-interfaces';
import {NumberCardChartComponent} from '../../../shared/charts/number-card-chart/number-card-chart.component';
import {ClickListComponent} from '../../../shared/charts/click-list/click-list.component';

export let STATICANALYSIS_CHARTS: IChart[] = [
  {
    title: 'Project Details',
    component: ClickListComponent,
    data: [],
    xAxisLabel: '',
    yAxisLabel: '',
    colorScheme: '',
  },
  {
    title: 'Issues',
    component: NumberCardChartComponent,
    data: [
      {
        name: 'Blocker Violations',
        value: 0,
      },
      {
        name: 'Critical Violations',
        value: 0,
      },
      {
        name: 'Major Violations',
        value: 0,
      },
      {
        name: 'Total Issues',
        value: 0,
      }
    ],
    xAxisLabel: '',
    yAxisLabel: '',
    colorScheme: 'flame',
  },
  {
    title: 'Coverage',
    component: GaugeChartComponent,
    data: {
      dataPoints: [
        {
          name: 'Code Coverage',
          value: 0,
        },
      ],
      units: '',
      min: 0,
      max: 100,
    } as IGaugeChartData,
    xAxisLabel: '',
    yAxisLabel: '',
    colorScheme: 'forest',
  },
  {
    title: 'Unit Test Metrics',
    component: ClickListComponent,
    data: [],
    xAxisLabel: '',
    yAxisLabel: '',
    colorScheme: '',
  },
];
