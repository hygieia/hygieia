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
  { path: 'viewer', component: CollectorItemViewerComponent, pathMatch: 'full'},
  { path: 'viewer/details/', component: CollectorItemDetailsComponent },
  { path: 'viewer/details/', component:  CollectorRefreshComponent},

  { path: 'viewer/ScopeOwner/:dashboardTitle/:componentId', component: GenericEvidenceComponent},
  { path: 'viewer/Scope/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'viewer/ChatOps/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'viewer/Cloud/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'viewer/InfraPerformance/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'viewer/Score/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'viewer/TEAM/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'viewer/Log/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'viewer/AutoDiscover/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'viewer/AgileTool/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'viewer/AppPerformance/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'viewer/Artifact/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'viewer/CMDB/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'viewer/Feature/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'viewer/Incident/:dashboardTitle/:componentId', component: GenericEvidenceComponent },
  { path: 'viewer/Product/:dashboardTitle/:componentId', component: GenericEvidenceComponent },

  { path: 'viewer/Audit/:dashboardTitle/:componentId', component: AuditEvidenceComponent, pathMatch: 'prefix' },
  { path: 'viewer/Build/:dashboardTitle/:componentId', component: BuildEvidenceComponent },
  { path: 'viewer/CodeQuality/:dashboardTitle/:componentId', component: CodeQualityEvidenceComponent },
  { path: 'viewer/Deployment/:dashboardTitle/:componentId', component: DeploymentEvidenceComponent },
  { path: 'viewer/LibraryPolicy/:dashboardTitle/:componentId', component: LibraryPolicyEvidenceComponent },
  { path: 'viewer/SCM/:dashboardTitle/:componentId', component: SCMEvidenceComponent },
  { path: 'viewer/StaticSecurityScan/:dashboardTitle/:componentId', component: StaticSecurityScanEvidenceComponent },
  { path: 'viewer/TestResult/:dashboardTitle/:componentId', component: TestEvidenceComponent },
  { path: 'viewer/Test/:dashboardTitle/:componentId', component: TestEvidenceComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CollectorItemRoutingModule {
  static components = [CollectorItemViewerComponent, CollectorItemDetailsComponent, CollectorRefreshComponent];
}
