import {Component} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router} from '@angular/router';

@Component({
  selector: 'app-user-search',
  imports: [
    ReactiveFormsModule
  ],
  standalone: true,
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
          Validators.pattern(/^(?=.*[a-zA-Z0-9]+)[a-zA-Z0-9_-]+$/)
        ])
    });
  }

  async searchForUser() {
    if(this.userSearchForm.pristine || this.userSearchForm.invalid) return;
    void this.router.navigate(['/profile', this.userSearchForm.value.username]);
    this.userSearchForm.reset();
  }

  get isFormPristine(): boolean {
    return this.userSearchForm.pristine;
  }

}
