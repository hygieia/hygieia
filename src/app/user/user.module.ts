import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { UserRoutingModule } from './user-routing.module';
import {SharedModule} from '../shared/shared.module';

@NgModule({
  declarations: [UserRoutingModule.components],
  imports: [
    ReactiveFormsModule,
    SharedModule,
    UserRoutingModule
  ]
})
export class UserModule { }
