import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { DashEditComponent } from '../../../../../shared/dash-edit/dash-edit.component';
import { DashTrashComponent } from '../../../../../shared/dash-trash/dash-trash.component';
import { UserDataService } from '../../../services/user-data.service';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import {GeneralFilterPipe} from '../../../../../shared/pipes/filter.pipe';
import {GeneralOrderByPipe} from '../../../../../shared/pipes/order-by.pipe';
import {PropertiesBuilderComponent} from './properties-builder.component';
import {GeneralDeleteComponent} from '../../../../../shared/modals/general-delete-modal/general-delete-modal.component';
import {CreateOrUpdateApiPropertiesComponent} from '../modal/create-or-update-api-properties/create-or-update-api-properties.component';
import {NgModule} from '@angular/core';
import {NgbModal, NgbModule} from '@ng-bootstrap/ng-bootstrap';

@NgModule({
  declarations: [PropertiesBuilderComponent, DashEditComponent, DashTrashComponent, GeneralFilterPipe,
    GeneralOrderByPipe, CreateOrUpdateApiPropertiesComponent,
    GeneralDeleteComponent],
  providers: [UserDataService, NgbModal],
  imports: [FormsModule, CommonModule, ReactiveFormsModule, NgbModule, HttpClientTestingModule],
  entryComponents: [
    PropertiesBuilderComponent,
    CreateOrUpdateApiPropertiesComponent,
    GeneralDeleteComponent
  ]
})
class TestModule { }

fdescribe('PropertiesBuilderComponent', () => {
  let component: PropertiesBuilderComponent;
  let fixture: ComponentFixture<PropertiesBuilderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [TestModule],
    })
      .compileComponents();

  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PropertiesBuilderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should loadServiceAccounts', () => {
    component.ngOnInit();
  });

  it('should add new api properties builder', () => {
    component.addNewApiPropertiesBuilder();
  });

  it('should edit api properties builder', () => {
    const collector = {
      name: 'test',
      properties: 'test'
    };
    component.editApiPropertiesBuilder(collector);
  });

  it('should delete properties', () => {
    component.deleteProperties('123');
  });

  it('should stringify', () => {
    component.stringifyObj({123: '123'});
  });

  it('should hit properkeys', () => {
    component.properKeys({123: '123'});
  });
});
