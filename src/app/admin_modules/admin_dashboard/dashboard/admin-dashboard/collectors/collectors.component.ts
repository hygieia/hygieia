import { Component, OnInit } from '@angular/core';
import { CollectorsService } from '../../../services/collectors.service';
import { CollectorItem } from '../model/collectors-item';

@Component({
  selector: 'app-collectors',
  templateUrl: './collectors.component.html',
  styleUrls: ['./collectors.component.scss']
})
export class CollectorsComponent implements OnInit {

  public collectors;

  constructor(private collectorsService: CollectorsService) { }

  ngOnInit() {
    this.collectorsService.getAllCollectors().subscribe(res => {
      this.collectors = res;
      console.log(this.collectors);
    })
  }

}
