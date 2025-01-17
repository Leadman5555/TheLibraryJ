// import {AbstractControl, FormGroup, ValidationErrors, ValidatorFn} from '@angular/forms';
//
// export function atLeastOneValidator(): ValidatorFn {
//   return (control: AbstractControl): ValidationErrors | null => {
//     const controls = (control as FormGroup).controls;
//     return Object.keys(controls).some(key => {
//       const control = controls[key];
//       console.log(control.value);
//       return control.value !== null && control.value !== undefined && control.value > 0;
//     }) ? null : {atLeastOne: true};
//   };
// }
