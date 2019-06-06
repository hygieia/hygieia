import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BuildDetailComponent } from './build-detail.component';

describe('BuildDetailComponent', () => {
  let component: BuildDetailComponent;
  let fixture: ComponentFixture<BuildDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BuildDetailComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BuildDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
