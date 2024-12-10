import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'timesMax'
})
export class TimesMaxPipe implements PipeTransform {

  transform(maxValue: number, value: number, maxTimes: number): Iterable<number> {
    const halfMaxTimes = maxTimes/2;
    let boundStart = value - halfMaxTimes;
    let boundEnd = value + halfMaxTimes;
    if (boundStart < 0) {
      boundStart = 0;
      boundEnd = maxTimes - 1;
    }
    if (boundEnd >= maxValue) {
      boundEnd = maxValue - 1;
      boundStart = Math.max(maxValue - maxTimes, 0);
    }
    return {
      [Symbol.iterator]: function* () {
      let n = boundStart;
      while (n < boundEnd) {
        yield ++n;
      }
    }};
  }

}
