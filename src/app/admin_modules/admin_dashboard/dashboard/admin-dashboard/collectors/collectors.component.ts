import { animate, state, style, transition, trigger } from '@angular/animations';
import { Component, OnInit } from '@angular/core';
import { CollectorsService } from '../../../services/collectors.service';

@Component({
  selector: 'app-collectors',
  templateUrl: './collectors.component.html',
  styleUrls: ['./collectors.component.scss'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ]
})
export class CollectorsComponent implements OnInit {

  public collectors;
  displayColumns: string[] = ['name', 'status'];

  isExpansionDetailRow = (i: number, row: object) => row.hasOwnProperty('detailRow');

  constructor(private collectorsService: CollectorsService) { }

  ngOnInit() {
    this.collectorsService.getAllCollectors().subscribe(res => {
      this.collectors = res;
    });
  }

  getFields(uniqueFields) {
    return Object.keys(uniqueFields);
  }

   // Converts build duration to HH:mm:ss format
   convertToReadable(time): string {
    return new Date(time).toUTCString();
  }

}
