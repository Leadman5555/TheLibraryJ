import { Component } from '@angular/core';
import {FormControl, FormGroup, NonNullableFormBuilder, ReactiveFormsModule} from '@angular/forms';
import {UserProfileData} from '../shared/user-profile-data';
import {ImageDropComponent} from '../../../shared/image-drop/image-drop.component';

@Component({
  selector: 'app-user-profile-edit',
  imports: [
    ImageDropComponent,
    ReactiveFormsModule
  ],
  templateUrl: './user-profile-edit.component.html',
  styleUrl: './user-profile-edit.component.css'
})
export class UserProfileEditComponent {
  constructor(private fb: NonNullableFormBuilder) {
    this.imageUpdateForm = this.fb.group({
      newImage: null
    });
  }

  userData! : UserProfileData;
  imageUpdateForm : FormGroup;
  get imageControl() : FormControl{
    return this.imageUpdateForm.get('newImage') as FormControl;
  }
  // statusUpdateForm : FormGroup;
  // usernameUpdateForm : FormGroup;
  // preferenceUpdateForm : FormGroup;


  updateProfileImage() {

  }
}
