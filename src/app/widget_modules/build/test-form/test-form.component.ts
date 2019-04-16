import {Component, OnInit, Input, EventEmitter, Output} from '@angular/core';
import { FormsModule, FormGroup, FormControl } from '@angular/forms';

@Component({
  selector: 'app-test-form',
  templateUrl: './test-form.component.html',
  styleUrls: ['./test-form.component.scss']
})
export class TestFormComponent implements OnInit {
  @Output() submitted = new EventEmitter<boolean>();
  sampleForm: FormGroup;
  submitClose = false;

  ngOnInit(): void {
    this.sampleForm = new FormGroup({
      'test-field': new FormControl(null)
    });
  }

  onSubmit() {
    this.submitted.emit();
    this.submitClose = true;
  }

}
