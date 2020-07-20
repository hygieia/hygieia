import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Component({
  selector: 'app-dashboard-count',
  templateUrl: './dashboard-count.component.html',
  styleUrls: ['./dashboard-count.component.scss']
})
export class DashboardCountComponent implements OnInit {
  dCount: any[] = [];
  view: any[] = [450, 200];
  private dashboardCountRoute = '/api/dashboard/count/';

  // options
  gradient = true;
  showLegend = false;
  // showLabels = false;
  // isDoughnut = false;
  label = ' Total dashboards';

  colorScheme = {
    domain: ['green', 'brown']
  };

  constructor(private http: HttpClient) {
    this.loadCounts();
  }

  ngOnInit(): void {}

  private loadCounts() {
    const counts = [];
    ['Team', 'Product'].forEach(type => {
      this.http.get(this.dashboardCountRoute + type).subscribe(count => counts.push({name: type, value: count}));
    });

    setTimeout(() => {
      this.dCount = counts;
    }, 200);
  }
}
