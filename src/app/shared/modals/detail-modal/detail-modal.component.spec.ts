import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailModalComponent } from './detail-modal.component';

describe('DetailModalComponent', () => {
  let component: DetailModalComponent;
  let fixture: ComponentFixture<DetailModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DetailModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
