import {Component, OnInit} from '@angular/core';
import {CollectorItemService} from '../collector-item.service';
import {IDashboardCI, ICollItem} from '../interfaces';
import {FormControl} from '@angular/forms';
import {debounceTime, distinctUntilChanged, isEmpty, switchMap} from 'rxjs/operators';
import {of} from 'rxjs';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {CollectorItemDetailsComponent} from './collector-item-details/collector-item-details.component';
import {CollectorRefreshComponent} from './collector-refresh/collector-refresh.component';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-collector-item',
  templateUrl: './collector-item-viewer.component.html',
  styleUrls: ['./collector-item-viewer.component.scss']
})
export class CollectorItemViewerComponent implements OnInit {
  dashboards: IDashboardCI[];
  queryField: FormControl = new FormControl();
  private ciTitle: string;

  public readonly collectors = [
    'SCM',
    'CMDB',
    'Incident',
    'Build',
    'Artifact',
    'Deployment',
    'AgileTool',
    'Feature',
    'TestResult',
    'ScopeOwner',
    'Scope',
    'CodeQuality',
    'Test',
    'StaticSecurityScan',
    'LibraryPolicy',
    'ChatOps',
    'Cloud',
    'Product',
    'AppPerformance',
    'InfraPerformance',
    'Score',
    'TEAM',
    'Audit',
    'Log',
    'AutoDiscover'
  ];
  private dashboardTitle: string;

  constructor(
    private ciViewerService: CollectorItemService,
    private modalService: NgbModal, private route: ActivatedRoute) { }

  ngOnInit() {
    this.dashboardTitle = this.route.snapshot.paramMap.get('title');
    // Query for a dashboard
    this.queryField.valueChanges.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      switchMap(() => {
        this.ciTitle = (this.queryField.value) ? this.queryField.value : this.ciTitle;
        if (!this.ciTitle) { return of([]); }
        const r = this.ciViewerService.getDashboardByCI(this.ciTitle);
        if (!r.pipe(isEmpty())) { return r; }
        return this.ciViewerService.getDashboardByTitle(this.ciTitle);
      })
    ).subscribe(response => {
      this.dashboards = response;
    });
    if (this.dashboardTitle) {
      this.queryField.setValue(this.dashboardTitle, {emitEvent: true});
    }
  }

  openDetails( collector: string, dashboardTitle: string, componentName: string, ci: ICollItem) {
    const modalRef = this.modalService.open(CollectorItemDetailsComponent);
    modalRef.componentInstance.collector = collector;
    modalRef.componentInstance.dashboardTitle = dashboardTitle;
    modalRef.componentInstance.componentName = componentName;
    modalRef.componentInstance.collectorDetails = this.maskSecureData(ci);
  }

  confirmRefresh(ci: ICollItem) {
    const modalRefresh = this.modalService.open(CollectorRefreshComponent);
    modalRefresh.componentInstance.collectorDetails = ci;
  }

  private maskSecureData(ci: ICollItem): ICollItem {
    if (ci.options.password) { ci.options.password = '******'; }
    if (ci.options.personalAccessToken) { ci.options.personalAccessToken = '******'; }
    return ci;
  }
}
