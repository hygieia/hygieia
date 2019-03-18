import { LayoutDirective } from './layout.directive';
import { TestBed, ComponentFixture } from '@angular/core/testing';
import { ViewContainerRef, Component } from '@angular/core';

@Component({
    template: `<ng-template appChart></ng-template>`
})
class TestLayoutDirectiveComponent {
}

describe('LayoutDirective', () => {
    let component: TestLayoutDirectiveComponent;
    let fixture: ComponentFixture<TestLayoutDirectiveComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [TestLayoutDirectiveComponent, LayoutDirective]
        });
        fixture = TestBed.createComponent(TestLayoutDirectiveComponent);
        component = fixture.componentInstance;
    });

    it('should create an instance', () => {
        expect(component).toBeTruthy();
    });
});
