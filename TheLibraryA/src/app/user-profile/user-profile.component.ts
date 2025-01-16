import {HttpClient} from '@angular/common/http';
import {Component, OnInit} from '@angular/core';
import {UserProfileData} from './user-profile-data';
import {parseDateArray} from '../shared/functions/parseData';
import {NgIf} from '@angular/common';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {logError} from '../shared/errorHandling/handleError';
import {ActivatedRoute, RouterLink} from '@angular/router';

@Component({
  selector: 'app-user-profile',
  imports: [
    NgIf,
    FormsModule,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css'
})

export class UserProfileComponent implements OnInit {

  private readonly baseUrl: string = 'http://localhost:8082/v0.9/na';

  private readonly baseAuthUrl: string = 'http://localhost:8082/v0.9';

  constructor(private http: HttpClient, private activatedRoute: ActivatedRoute) {
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

  ngOnInit(): void {
    const username: string = this.activatedRoute.snapshot.params['username'];
    if (username)
      this.http.get<UserProfileData>(this.baseUrl + '/user/' + username).subscribe({
          next: (userProfile) => {
            this.userData = userProfile;
            this.userPresent = true;
          },
          error: (error) => {
            logError(error);
          }
        }
      )
  }

  readonly preferenceArray: string[] = [];
  readonly rankArray: string[] = [];
  readonly progressArray: number[] = [3, 5, 10, 20, 40, 60, 100, 200, 500, 1000, 3333];

  userData!: UserProfileData;
  userPresent: boolean = false;

  protected readonly parseDate = parseDateArray; // parseDate is removed as it's not available.
  userSearchForm: FormGroup;

  searchForUser() {
    if (this.userSearchForm.invalid || this.userSearchForm.pristine) return;
    this.http.get<UserProfileData>(this.baseUrl + '/user/' + this.userSearchForm.value.username).subscribe({
      next: (userProfile) => {
        this.userData = userProfile;
      },
      error: (error) => {
        logError(error);
      }
    });
    this.userSearchForm.reset();
  }

}
