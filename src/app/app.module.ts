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

// Component Imports
import {FormModalComponent} from './shared/modals/form-modal/form-modal.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {BuildConfigFormComponent} from './widget_modules/build/build-config-form/build-config-form.component';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    BrowserAnimationsModule,
    CoreModule,
    NgbModule,
    SharedModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent],
  entryComponents: [
    FormModalComponent
  ]
})
export class AppModule {
}
