import {
  Component, OnDestroy,
  OnInit,
} from '@angular/core';
import {groupBy, map, mergeMap, takeUntil, toArray} from 'rxjs/operators';
import {NfrrService} from '../nfrr.service';
import {from, of, Subject, zip} from 'rxjs';
import {IAudit} from '../../../shared/interfaces';

export class ChartDataModel {
  public data: SeriesModel[];
  constructor(data: SeriesModel[]) {
    this.data = data;
  }
}

export class SeriesModel {
  public name: string;
  public series: SeriesChildModel[];
  constructor(name: string, series: SeriesChildModel[]) {
    this.name = name;
    this.series = series;
  }
}

export class SeriesChildModel {
  public name: string;
  public value: number;
  constructor(name: string, value: number) {
    this.name = name;
    this.value = value;
  }
}

@Component({
  selector: 'app-nfrr-view',
  templateUrl: './nfrr-view.component.html',
  styleUrls: ['./nfrr-view.component.scss']
})
export class NfrrViewComponent implements OnInit, OnDestroy {

  ngxData: ChartDataModel = { data: []};
  ngxDataNormalized: ChartDataModel = { data: []};
  private destroyed$ = new Subject();
  lobs = new Set<string>();
  lastAudited: Date;
  isLoading = true;
  isEmpty = false;
  auditAll: IAudit[] = [];

  dataLabelFormatting(value: any) {
    return value;
  }

  constructor(private nfrrService: NfrrService) {
    this.getAuditMetricsAll();
  }

  ngOnInit() {
  }

  onOptionsSelected($event) {
    const lob: string = $event.target.value;
    this.isLoading = true;
    this.isEmpty = false;
    lob === 'All' ? this.getAuditMetricsAll() : this.getAuditMetricsByLob(lob);
  }

  getAuditMetricsAll() {
    if (this.auditAll.length > 0) {
      this.transformToChartData(this.auditAll);
      return;
    }
    this.nfrrService.getAuditMetricsAll().pipe(takeUntil(this.destroyed$)).subscribe(result => {
      if (result.length === 0) {
        this.isLoading = false;
        this.isEmpty = true;
        return;
      }
      this.lastAudited = new Date(result[0].timestamp);
      const lobs = new Set<string>();
      result.forEach(r => {
        if (r.lineOfBusiness && r.lineOfBusiness.trim() !== '' && r.lineOfBusiness !== 'All') {
          lobs.add(r.lineOfBusiness);
        }
      });
      this.lobs = lobs;
      this.auditAll = result;
      this.transformToChartData(result as IAudit[]);
    });
  }

  getAuditMetricsByLob(lob: string) {
    const audits = this.auditAll.filter(audit => audit.lineOfBusiness === lob);
    this.transformToChartData(audits);
  }

  onSelect(event) {
  }

  transformToChartData(audits: IAudit[]) {
    if (audits === undefined || audits.length === 0) {
      this.isEmpty = true;
      this.isLoading = false;
      return;
    }
    const allAudits$ = from(audits).pipe(
      groupBy(audit => audit.auditType, a => ({ auditStatus: a.auditStatus, auditTypeStatus: a.auditTypeStatus})),
      mergeMap(group => zip(of(group.key), group.pipe(toArray()))),
      takeUntil(this.destroyed$)
    );

    allAudits$.pipe(map(arr => ({name: arr[0], series: [
          {name: 'AUDIT_PASS', value: arr[1].reduce((a, c) => c.auditStatus === 'OK' ? a + 1 : a, 0)},
          {name: 'AUDIT_FAIL', value: arr[1].reduce((a, c) => c.auditStatus === 'FAIL' ? a + 1 : a, 0)},
          {name: 'NA', value: arr[1].reduce((a, c) => c.auditStatus === 'NA' ? a + 1 : a, 0)}
        ]})),
      toArray(), takeUntil(this.destroyed$)).subscribe(result => {
      this.ngxData.data = [...result];
    });

    allAudits$.pipe(map(arr => ({name: arr[0], series: [
          {name: 'MEASURABLE', value: arr[1].reduce((a, c) => c.auditTypeStatus === 'OK' ? a + 1 : a, 0)},
          {name: 'NO_DATA', value: arr[1].reduce((a, c) => c.auditTypeStatus === 'NO_DATA' ? a + 1 : a, 0)},
          {name: 'NOT_CONFIGURED', value: arr[1].reduce((a, c) => c.auditTypeStatus === 'NOT_CONFIGURED' ? a + 1 : a, 0)},
          {name: 'ERROR', value: arr[1].reduce((a, c) => c.auditTypeStatus === 'ERROR' ? a + 1 : a, 0)}
        ]})),
      toArray(), takeUntil(this.destroyed$)).subscribe(result => {
      this.ngxDataNormalized.data = [...result];
      this.isLoading = false;
      this.isEmpty = false;
    });
  }

  auditTypeToReadable(value: any) {
    let type: string;
    switch (value) {
      case 'TEST_RESULT':
        type = 'Functional Testing'; break;
      case 'PERF_TEST':
        type = 'Performance Testing'; break;
      case 'CODE_REVIEW':
        type = 'Peer Review'; break;
      case 'CODE_QUALITY':
        type = 'Static Code Analysis'; break;
      case 'STATIC_SECURITY_ANALYSIS':
        type = 'Application Security'; break;
      case 'ARTIFACT':
        type = 'Artifact'; break;
      case 'DEPLOY':
        type = 'Deployment Scripts'; break;
      case 'LIBRARY_POLICY':
        type = 'Open Source'; break;
      case 'ALL':
        type = 'Data error'; break;
      default:
        type = value;
    }
    return type;
  }

  ngOnDestroy(): void {
    this.destroyed$.next();
    this.destroyed$.complete();
  }
}
