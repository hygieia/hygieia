import { Component, OnInit } from '@angular/core';
import {DashboardService} from '../shared/dashboard.service';

@Component({
  selector: 'app-landing-page',
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.scss']
})
export class LandingPageComponent implements OnInit {

  constructor( private dashboardService: DashboardService) { }

  ngOnInit() {
    this.dashboardService.loadCounts();
  }
}
