import {BrowserModule} from '@angular/platform-browser';
import {ErrorHandler, NgModule} from '@angular/core';

// 3rd Party imports
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';

// App imports
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {CoreModule} from './core/core.module';
import {SharedModule} from './shared/shared.module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {
  NbThemeModule,
  NbLayoutModule,
  NbActionsModule,
  NbUserModule,
  NbSearchModule,
  NbIconModule
} from '@nebular/theme';
import { NbEvaIconsModule } from '@nebular/eva-icons';
import {NgxUIModule} from '@swimlane/ngx-ui';
import {GlobalErrorHandler} from './app.error.handler';
import {SsoAuthGuard} from './user/sso/sso.authguard';

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    BrowserAnimationsModule,
    CoreModule,
    NgbModule,
    SharedModule,
    NbThemeModule.forRoot({name: 'dark'}),
    NbLayoutModule,
    NbEvaIconsModule,
    NbActionsModule,
    NbUserModule,
    NgxUIModule,
    NbSearchModule,
    NbIconModule
  ],
  providers: [{ provide: ErrorHandler, useClass: GlobalErrorHandler }, SsoAuthGuard],
  bootstrap: [AppComponent],
  exports: []
})
export class AppModule {
}
