import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

// 3rd Party imports
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';

// App imports
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {CoreModule} from './core/core.module';
import {SharedModule} from './shared/shared.module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

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
    SharedModule
  ],
  bootstrap: [AppComponent],
  exports: []
})
export class AppModule {
}
