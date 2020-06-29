import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { CollectorItemViewerComponent } from './collector-item-viewer/collector-item-viewer.component';
import {CollectorItemDetailsComponent} from './collector-item-viewer/collector-item-details/collector-item-details.component';
import {GenericEvidenceComponent} from './collector-item-viewer/generic-evidence/generic-evidence.component';
import {CollectorRefreshComponent} from './collector-item-viewer/collector-refresh/collector-refresh.component';
import {TestEvidenceComponent} from './collector-item-viewer/test-evidence/test-evidence.component';
import {LibraryPolicyEvidenceComponent} from './collector-item-viewer/library-policy-evidence/library-policy-evidence.component';
import {SCMEvidenceComponent} from './collector-item-viewer/scm-evidence/scm-evidence.component';
import {DeploymentEvidenceComponent} from './collector-item-viewer/deployment-evidence/deployment-evidence.component';
import {CodeQualityEvidenceComponent} from './collector-item-viewer/code-quality-evidence/code-quality-evidence.component';
import {BuildEvidenceComponent} from './collector-item-viewer/build-evidence/build-evidence.component';
import {AuditEvidenceComponent} from './collector-item-viewer/audit-evidence/audit-evidence.component';
// tslint:disable-next-line:max-line-length
import {StaticSecurityScanEvidenceComponent} from './collector-item-viewer/static-security-scan-evidence/static-security-scan-evidence.component';

const routes: Routes = [
  { path: '', component: CollectorItemViewerComponent},
  { path: 'details/', component: CollectorItemDetailsComponent },
  { path: 'details/', component:  CollectorRefreshComponent},

  { path: 'ScopeOwner/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'Scope/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'ChatOps/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'Cloud/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'InfraPerformance/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'Score/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'TEAM/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'Log/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'AutoDiscover/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'AgileTool/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'AppPerformance/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'Artifact/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'CMDB/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'Feature/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'Incident/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'Product/:dashboardTitle/:componentId', component: GenericEvidenceComponent },

  { path: 'Audit/:dashboardTitle/:componentId', component: AuditEvidenceComponent },
  { path: 'Build/:dashboardTitle/:componentId', component: BuildEvidenceComponent },
  { path: 'CodeQuality/:dashboardTitle/:componentId', component: CodeQualityEvidenceComponent },
  { path: 'Deployment/:dashboardTitle/:componentId', component: DeploymentEvidenceComponent },
  { path: 'LibraryPolicy/:dashboardTitle/:componentId', component: LibraryPolicyEvidenceComponent },
  { path: 'SCM/:dashboardTitle/:componentId', component: SCMEvidenceComponent },
  { path: 'StaticSecurityScan/:dashboardTitle/:componentId', component: StaticSecurityScanEvidenceComponent },
  { path: 'TestResult/:dashboardTitle/:componentId', component: TestEvidenceComponent },
  { path: 'Test/:dashboardTitle/:componentId', component: TestEvidenceComponent }

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CollectorItemRoutingModule {
  static components = [CollectorItemViewerComponent, CollectorItemDetailsComponent, CollectorRefreshComponent];
}
