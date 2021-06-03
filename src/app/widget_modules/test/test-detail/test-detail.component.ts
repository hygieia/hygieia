import { Component, OnInit, Input, Type } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-test-detail',
  templateUrl: './test-detail.component.html',
  styleUrls: ['./test-detail.component.scss']
})
export class TestDetailComponent implements OnInit {

  @Input() detailView: Type<any>;

  public data: any;

  constructor(
    public activeModal: NgbActiveModal
  ) { }

  ngOnInit() { }

  @Input()
  set detailData(data: any) {
    this.data = data.data;
  }

  hasData() {
    return this.data !== undefined;
  }

}
