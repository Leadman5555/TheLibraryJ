import {AbstractControl, ValidationErrors, ValidatorFn} from '@angular/forms';

export function passwordMatchValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (control.get('newPassword')?.value !== control.get('repeatPassword')?.value) return {passwordMismatch: true};
    return null;
  };
}
