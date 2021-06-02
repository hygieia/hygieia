import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
    selector: 'app-build-viewer',
    templateUrl: './build-viewer.component.html',
    styleUrls: ['./build-viewer.component.scss']
  })
  export class BuildViewerComponent {
      buildId: string;

      constructor(public router: Router) {}

      searchBuild() {
        this.router.navigate([`/build/${this.buildId}`]);
      }
  }
