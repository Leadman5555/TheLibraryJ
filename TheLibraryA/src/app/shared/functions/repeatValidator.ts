import {AbstractControl, ValidationErrors, ValidatorFn} from '@angular/forms';

export function repeatValidator(shouldMatch: string): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value: string | null = control.value;
    if(!value) return {required: true};
    return value === shouldMatch ? null : {repeat: true};
  }
}
