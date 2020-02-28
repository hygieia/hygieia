import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { DeployWidgetComponent } from './deploy-widget.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterModule } from '@angular/router';

describe('DeployWidgetComponent', () => {
  let component: DeployWidgetComponent;
  let fixture: ComponentFixture<DeployWidgetComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule,RouterModule.forRoot([])],
      declarations: [ DeployWidgetComponent ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeployWidgetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
