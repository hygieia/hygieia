import { ClickListComponent } from 'src/app/shared/charts/click-list/click-list.component';
import { NumberCardChartComponent } from 'src/app/shared/charts/number-card-chart/number-card-chart.component';
import { IChart } from 'src/app/shared/interfaces';

export let FEATURE_CHARTS: IChart[] = [
  {
    title: 'Project Details',
    component: ClickListComponent,
    data: [],
    xAxisLabel: '',
    yAxisLabel: '',
    colorScheme: {}
  },
  {
    title: 'Iteration Summary',
    component: NumberCardChartComponent,
    data: [
      {
        name: 'OPEN',
        value: 0
      },
      {
        name: 'WIP',
        value: 0
      },
      {
        name: 'DONE',
        value: 0
      },
    ],
    xAxisLabel: '',
    yAxisLabel: '',
    colorScheme: 'vivid'
  },
  {
    title: 'Epics/Issues in Progress and Points',
    component: ClickListComponent,
    data: [],
    xAxisLabel: '',
    yAxisLabel: '',
    colorScheme: {}
  },
];
