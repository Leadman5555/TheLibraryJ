import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  standalone: true,
  name: 'timesMax'
})
export class TimesMaxPipe implements PipeTransform {

  transform(maxValue: number, value: number): Iterable<number> {
    return {
      [Symbol.iterator]: function* (): Iterator<number> {
        let n = value;
        while (n < maxValue) {
          yield ++n;
        }
      }};
  }

}
