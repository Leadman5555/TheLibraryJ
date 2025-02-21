import {Directive, HostListener} from '@angular/core';

@Directive({
  selector: '[appBlockRouterLink]'
})
export class BlockRouterLinkDirective {
  @HostListener('click', ['$event'])
  onClick(event: MouseEvent): void {
    event.preventDefault();
    event.stopPropagation();
    console.log('Default routerLink navigation blocked!');
  }
}
