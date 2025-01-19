import { Component } from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgIf} from '@angular/common';
import {UserProfileData} from '../user-profile-data';
import {catchError} from 'rxjs';
import {handleError} from '../../shared/errorHandling/handleError';
import {Router} from '@angular/router';

@Component({
  selector: 'app-user-search',
  imports: [
    NgIf,
    ReactiveFormsModule
  ],
  templateUrl: './user-search.component.html',
  styleUrl: './user-search.component.css'
})
export class UserSearchComponent {
  userSearchForm: FormGroup;

  constructor(private router: Router) {
    this.userSearchForm = new FormGroup({
      username: new FormControl('',
        [
          Validators.required,
          Validators.minLength(5),
          Validators.maxLength(20),
          Validators.pattern('^[a-zA-Z0-9_-]+$')
        ])
    });
  }

  searchForUser() {
    if(this.userSearchForm.pristine || this.userSearchForm.invalid) return;
    const username = this.userSearchForm.value.username;
    this.router.navigate(['/profile', username]);
    this.userSearchForm.reset();
  }
}
