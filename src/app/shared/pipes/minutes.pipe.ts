import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'minutes' })
export class MinutesPipe implements PipeTransform {
  transform(value: number): string {
    const minutes = Math.floor(value / 60000);
    const seconds = ((value % 60000) / 1000);
    return (seconds === 60 ? (minutes + 1) + ':00' : minutes + ':' + (seconds < 10 ? '0' : '') + seconds.toFixed(0));
  }
}
