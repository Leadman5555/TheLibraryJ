import {AbstractControl, FormArray, FormGroup, ValidationErrors, ValidatorFn} from '@angular/forms';

export function atLeastOneValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => (control as FormArray).value.some((value: boolean) => value) ? null : {atLeastOneRequired: true}
}
