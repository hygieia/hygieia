import { IChart } from 'src/app/shared/interfaces';

import {NumberCardChartComponent} from '../../../shared/charts/number-card-chart/number-card-chart.component';
import {ClickListComponent} from '../../../shared/charts/click-list/click-list.component';
import {PieGridChartComponent} from '../../../shared/charts/pie-grid-chart/pie-grid-chart.component';
import {IPieGridData} from '../../../shared/charts/pie-grid-chart/pie-grid-interfaces';
import {DataItem} from '../../../shared/ngx-charts/pie-grid/data-item.interfaces';

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
    title: 'Coverage',
    component: PieGridChartComponent,
    data: {
      results: [
        {
          name: '',
          value: 0,
        }
      ] as DataItem[],
      designatedTotal: 100.0,
      label: 'Lines of Code',
      useCustomLabelValue: true,
      customLabelValue: 0,
    } as IPieGridData,
    xAxisLabel: '',
    yAxisLabel: '',
    scaleFactor: 0.85,
    colorScheme: 'forest',
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
    scaleFactor: 0.55,
    colorScheme: 'flame',
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
