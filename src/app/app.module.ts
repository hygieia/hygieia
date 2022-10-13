import { BrowserModule } from '@angular/platform-browser';
import { CUSTOM_ELEMENTS_SCHEMA, ErrorHandler, NgModule } from '@angular/core';

// 3rd Party imports
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

// App imports
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CoreModule } from './core/core.module';
import { SharedModule } from './shared/shared.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {
  NbThemeModule,
  NbLayoutModule,
  NbActionsModule,
  NbUserModule,
  NbSearchModule,
  NbIconModule
} from '@nebular/theme';
import { NbEvaIconsModule } from '@nebular/eva-icons';
import { GlobalErrorHandler } from './app.error.handler';
import { SsoAuthGuard } from './user/sso/sso.authguard';
import { CommonModule } from '@angular/common';
import { MatCommonModule } from '@angular/material/core';

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    BrowserAnimationsModule,
    CommonModule,
    CoreModule,
    NgbModule,
    SharedModule,
    MatCommonModule,
    NbThemeModule.forRoot({name: 'dark'}),
    NbLayoutModule,
    NbEvaIconsModule,
    NbActionsModule,
    NbUserModule,
    NbSearchModule,
    NbIconModule
  ],
  providers: [{ provide: ErrorHandler, useClass: GlobalErrorHandler }, SsoAuthGuard],
  bootstrap: [AppComponent],
  exports: [CommonModule, MatCommonModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AppModule {
}
