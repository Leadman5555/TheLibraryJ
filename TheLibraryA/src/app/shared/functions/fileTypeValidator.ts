import {AbstractControl, ValidationErrors, ValidatorFn} from '@angular/forms';

function fileTypeValidator(allowedFileTypes: string[], allowedFileExtensions: string[]): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if(control.value === null) return null;
    const files: File[] = control.value instanceof FileList ? Array.from(control.value) : [control.value];
    if (files.length === 0) return null;
    if (Array.from(files).every(file =>
      allowedFileExtensions.includes(file.name.substring(file.name.lastIndexOf('.') + 1)) &&
      allowedFileTypes.includes(file.type))
    ) return null;
    return {invalidFileType: true};

  }
}

const allowedTextFileTypes = ['text/plain', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'application/vnd.oasis.opendocument.text'];
const allowedTextFileExtensions = ['txt', 'doc', 'docx', 'odt'];

const allowedImageFileTypes = ['image/jpeg', 'image/png', 'image/webp'];
const allowedImageFileExtensions = ['jpg', 'jpeg', 'png', 'webp'];

export function textFileTypesValidator(): ValidatorFn {
  return fileTypeValidator(allowedTextFileTypes, allowedTextFileExtensions);
}

export function imageFileTypeValidator(): ValidatorFn {
  return fileTypeValidator(allowedImageFileTypes, allowedImageFileExtensions);
}
