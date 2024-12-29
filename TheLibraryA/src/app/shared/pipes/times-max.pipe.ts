import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'timesMax'
})
export class TimesMaxPipe implements PipeTransform {

  transform(maxValue: number, value: number): Iterable<number> {
    return {
      [Symbol.iterator]: function* () {
        let n = value;
        while (n < maxValue) {
          yield ++n;
        }
      }};
  }

}
