import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {CollectorItemService} from '../../collector-item.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-evidence',
  templateUrl: './generic-evidence.component.html',
  styleUrls: ['./generic-evidence.component.scss']
})
export class GenericEvidenceComponent implements OnInit, OnDestroy {

  collectorEvidenceDetails: any[];
  dashboardTitle: string;
  private componentId: string;
  componentName: string;
  collector: string;

  // Reference to the subscription used to refresh the viewer
  private intervalRefreshSubscription: Subscription;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private ciViewerService: CollectorItemService) { }

  ngOnInit() {
    this.dashboardTitle = this.route.snapshot.paramMap.get('dashboardTitle');
    this.componentId = this.route.snapshot.paramMap.get('componentId');
    this.componentName = this.route.snapshot.paramMap.get('componentName');
    if (this.route.snapshot.url) {
      this.collector = this.route.snapshot.url[0].path;
    }
    this.intervalRefreshSubscription = this.ciViewerService.getCollectorItemDetails(
      this.dashboardTitle, this.componentId, this.collector).subscribe(response => {
        this.collectorEvidenceDetails = response;
      });
  }

  ngOnDestroy() {
    if (this.intervalRefreshSubscription) {
      this.intervalRefreshSubscription.unsubscribe();
    }
  }

  getJsonHtml( obj ) {
    return JSON.stringify(obj, undefined, 4);
  }

  openCollectorViewer(dTitle: string) {
    this.router.navigate(['/collectorItem/viewer', {title : dTitle}]);
  }
}
