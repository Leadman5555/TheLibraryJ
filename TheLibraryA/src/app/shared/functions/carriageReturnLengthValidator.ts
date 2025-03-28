import {AbstractControl, ValidationErrors, ValidatorFn} from '@angular/forms';

export function carriageReturnLengthValidator(min: number, max: number): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value: string | null = control.value;
    if (!value) return null;
    const length = value.length;
    if (length < min) return { minLength: { requiredLength: min, actualLength: length } };
    const newlineCount: number = (value.match(/\n/g) || []).length;
    const adjustedLength: number = length + newlineCount * 2;
    if (adjustedLength > max) return { maxLength: { requiredLength: max, actualLength: adjustedLength } };
    return null;
  };
}
