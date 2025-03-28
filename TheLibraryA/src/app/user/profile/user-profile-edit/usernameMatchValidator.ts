import {AbstractControl, ValidationErrors, ValidatorFn} from '@angular/forms';

export function usernameMatchValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (control.get('newUsername')?.value !== control.get('repeatUsername')?.value) return {usernameMismatch: true};
    return null;
  };
}
