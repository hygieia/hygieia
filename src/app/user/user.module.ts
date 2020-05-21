import { NgModule } from '@angular/core';
import { UserRoutingModule } from './user-routing.module';
import { SharedModule } from '../shared/shared.module';

@NgModule({
  declarations: [UserRoutingModule.components],
  imports: [
    UserRoutingModule,
    SharedModule,
  ]
})
export class UserModule { }
