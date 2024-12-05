import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'timesMax'
})
export class TimesMaxPipe implements PipeTransform {

  transform(value: number, maxTimes: number): any {
    const iter = <Iterable<any>> {};
    const bound = Math.max(value, maxTimes);
    iter[Symbol.iterator] = function* () {
      let n = 0;
      while(n < bound) yield ++n;
    }
    return iter;
  }

}
