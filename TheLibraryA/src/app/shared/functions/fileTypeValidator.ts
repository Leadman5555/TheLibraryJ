import {AbstractControl, ValidationErrors, ValidatorFn} from '@angular/forms';
import {File} from 'node:buffer';

export function fileTypeValidator(allowedFileTypes: string[]): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const files : File[] = control.value;
    if (files.length === 0) return null;
    const invalidFile = files.find(file => !allowedFileTypes.includes(file.type));
    if (invalidFile) return { invalidFileType: true };
    return null;
  }
}
