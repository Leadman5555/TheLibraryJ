import {AbstractControl, ValidationErrors, ValidatorFn} from '@angular/forms';

export function integerValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (control.value) {
      const value: string = control.value.toString();
      const index = value.indexOf('.');
      return index < 0 ? null : {int: true}
    }
    return null;
  }
}

export function stepValidator(maxStep: number): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (control.value) {
      const value: string = control.value.toString();
      const index = value.indexOf('.');
      if (index < 0) return null;
      return value.length - index > maxStep ? {maxStep: true} : null;
    }
    return null;
  }
}
