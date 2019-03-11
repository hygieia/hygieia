import { LayoutDirective } from './layout.directive';
import { TestBed } from '@angular/core/testing';
import { ViewContainerRef } from '@angular/core';

describe('LayoutDirective', () => {
    it('should create an instance', () => {
        const viewContainerRef = TestBed.get(ViewContainerRef) as ViewContainerRef;
        const directive = new LayoutDirective(viewContainerRef);
        expect(directive).toBeTruthy();
    });
});
