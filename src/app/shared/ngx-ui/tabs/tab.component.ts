import {
  Component,
  Input,
  TemplateRef,
  ContentChild,
  ElementRef,
  Renderer2,
  OnInit,
  ViewChild,
  ChangeDetectionStrategy,
  ChangeDetectorRef, HostBinding
} from '@angular/core';
import { IfTabActiveDirective } from './if-tab-active.directive';

// This component is based on the tab component from ngx-ui
// https://github.com/swimlane/ngx-ui/blob/master/projects/swimlane/ngx-ui/src/lib/components/tabs/tab.component.ts
@Component({
  selector: 'app-tab',
  templateUrl: './tab.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TabComponent implements OnInit {
  @HostBinding('class') class = 'app-tab';

  @Input() title = '';
  @Input() label: string | TemplateRef<any> = '';
  @Input() active = false;
  @Input() disabled = false;
  @ViewChild('labelIsStringTmpl', { static: true }) labelStringTemplate;
  @ContentChild(IfTabActiveDirective, { static: true }) template: IfTabActiveDirective;
  labelTemplate: TemplateRef<any>;

  constructor(private cdr: ChangeDetectorRef, private renderer: Renderer2, private elRef: ElementRef) {}

  ngOnInit() {
    // backwards compatibility
    if (this.title) {
      this.label = this.title;
      this.renderer.removeAttribute(this.elRef.nativeElement, 'title');
    }

    this.labelTemplate = typeof this.label === 'string' ? this.labelStringTemplate : this.label;
  }

  detectChanges() {
    this.cdr.detectChanges();
  }
}
