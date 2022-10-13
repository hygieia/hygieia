import {CUSTOM_ELEMENTS_SCHEMA, NgModule, Optional, SkipSelf} from '@angular/core';
import { RouterModule } from '@angular/router';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { ModuleLoadedOnceGuard } from './module-loaded-once.guard';
import { AuthService } from './services/auth.service';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { SharedModule } from '../shared/shared.module';
import { NbLayoutModule, NbActionsModule } from '@nebular/theme';
// import { NavbarComponent } from 'src/app/core/navbar/navbar.component';
import { CommonModule } from '@angular/common';

@NgModule({
  declarations: [],
  imports: [
    HttpClientModule,
    RouterModule,
    SharedModule,
    NbLayoutModule,
    NbActionsModule,
    CommonModule,
  ],
  exports: [RouterModule, HttpClientModule],
  providers: [AuthService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true,
    }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class CoreModule extends ModuleLoadedOnceGuard {
  // Looks for the module in the parent injector to verify that it has been loaded once
  constructor(@Optional() @SkipSelf() parentModule: CoreModule) {
    super(parentModule);
  }
}
