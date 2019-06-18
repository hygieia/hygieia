import { Component, ViewEncapsulation } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { DetailModalComponent } from '../../modals/detail-modal/detail-modal.component';
import { ChartComponent } from '../chart/chart.component';
import { ILineChartData } from './ILineChartData';

@Component({
  selector: 'app-line-chart',
  templateUrl: './line-chart.component.html',
  styleUrls: ['./line-chart.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class LineChartComponent extends ChartComponent {
  constructor(private modalService: NgbModal) {
    super();
  }
  // options
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showLegend = false;
  tooltipDisabled = false;
  showXAxisLabel = false;
  showYAxisLabel = false;
  trimYAxisTicks = false;
  timeline = false;
  yAxisTickFormatting: (val: number) => string = this.formatInteger;
  xAxisTickFormatting: (val: Date) => string = this.formatToDayAndMonth;

  formatInteger(val: number): string {
    if (Number.isInteger(val)) {
      return val.toFixed(0);
    }
    return '';
  }

  formatToDayAndMonth(val: Date): string {
    return (val.getMonth() + 1) + '/' + val.getDate();
  }

  onSelect(event) {
    if (this.data && (this.data as ILineChartData).detailComponent) {
      const modalRef = this.modalService.open(DetailModalComponent);
      modalRef.componentInstance.title = 'Details';
      (modalRef.componentInstance as DetailModalComponent).detailView = this.data.detailComponent;
    }
  }
}
