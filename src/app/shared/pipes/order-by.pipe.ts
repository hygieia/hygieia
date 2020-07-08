import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'adminOrderBy',
  pure: true
})
export class AdminOrderByPipe implements PipeTransform {

  transform(value: any[], propertyName: string): any[] {
    if (propertyName) {
      return value.sort((a: any, b: any) => {
        if (!a[propertyName] || !b[propertyName]) {
          return 0;
        }
        return a[propertyName].localeCompare(b[propertyName]);
      });
    } else {
      return value;
    }
  }

}
