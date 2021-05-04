import {IChart} from '../../../shared/interfaces';
import {ClickListComponent} from '../../../shared/charts/click-list/click-list.component';

export let INFRA_SCAN_CHARTS: IChart[] =  [
  {
    title: 'Vulnerabilities',
    component: ClickListComponent,
    data: [],
    xAxisLabel: '',
    yAxisLabel: '',
    colorScheme: {}
  },
];
