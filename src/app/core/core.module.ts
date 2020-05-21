import {NgModule, Optional, SkipSelf} from '@angular/core';
import { RouterModule } from '@angular/router';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { ModuleLoadedOnceGuard } from './module-loaded-once.guard';
import { AuthService } from './services/auth.service';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { SharedModule } from '../shared/shared.module';

@NgModule({
  declarations: [],
  imports: [
    HttpClientModule,
    RouterModule,
    SharedModule
  ],
  exports: [RouterModule, HttpClientModule],
  providers: [AuthService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true,
    }]
})
export class CoreModule extends ModuleLoadedOnceGuard {
  // Looks for the module in the parent injector to verify that it has been loaded once
  constructor(@Optional() @SkipSelf() parentModule: CoreModule) {
    super(parentModule);
  }
}
