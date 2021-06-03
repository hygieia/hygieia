import { ClickListComponent } from 'src/app/shared/charts/click-list/click-list.component';
import { IChart } from 'src/app/shared/interfaces';

export let DEPLOY_CHARTS: IChart[] = [
  {
    title: 'Last 10 Deployments',
    component: ClickListComponent,
    data: [],
    xAxisLabel: '',
    yAxisLabel: '',
    colorScheme: {}
  },
];
