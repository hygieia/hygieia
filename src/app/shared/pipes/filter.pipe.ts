import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'generalFilter'
})
export class GeneralFilterPipe implements PipeTransform {
  transform(items: any[], agrs?: any): any[] {
    if (!items) {
      return [];
    }
    if (typeof agrs === 'string') {
      const searchText = agrs.toLowerCase();
      if (!searchText) {
        return items;
      }
      return items.filter(it => {
        return it.toLowerCase().includes(searchText);
      });
    } else {
      const keys = Object.keys(agrs);
      if (keys.length > 0) {
        const list = items.filter( (obj) => {
          return keys.every( (c) => {
            if (Array.isArray(obj[c])) {
              const isNot = agrs[c].indexOf('!') !== -1;
              if (isNot) {
                return obj[c].indexOf(agrs[c]) === -1;
              } else {
                return obj[c].indexOf(agrs[c]) !== -1;
              }
            } else {
              return obj[c].toLowerCase().indexOf(agrs[c].toLowerCase()) !== -1;
            }
          });
        });
        return list;
      } else {
        return items;
      }
    }

  }
}
