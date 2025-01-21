import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {UserProfileData} from '../shared/user-profile-data';
import {ImageDropComponent} from '../../../shared/image-drop/image-drop.component';
import {NgForOf, NgIf} from '@angular/common';
import {usernameMatchValidator} from './usernameMatchValidator';
import {preferenceArray, progressArray, rankArray} from '../shared/rankTitles';
import {identifyByIndex} from '../../../shared/functions/indentify';
import {ProgressBarComponent} from '../../../shared/progress-bar/progress-bar.component';
import {catchError} from 'rxjs';
import {handleError} from '../../../shared/errorHandling/handleError';
import {UserProfileService} from '../user-profile.service';
import {UserAuthService} from '../../userAuth/user-auth.service';
import {RouterLink} from '@angular/router';
import {
  UserPreferenceUpdateRequest, UserPreferenceUpdateResponse,
  UserProfileImageUpdateResponse, UserRankUpdateResponse, UserStatusUpdateRequest,
  UserUsernameUpdateRequest,
  UserUsernameUpdateResponse
} from './dto/UserUpdateDtos';

@Component({
  selector: 'app-user-profile-edit',
  imports: [
    ImageDropComponent,
    ReactiveFormsModule,
    NgIf,
    NgForOf,
    ProgressBarComponent,
    RouterLink
  ],
  templateUrl: './user-profile-edit.component.html',
  styleUrl: './user-profile-edit.component.css'
})
export class UserProfileEditComponent implements OnInit {
  constructor(private fb: NonNullableFormBuilder, private userProfileService: UserProfileService, private userAuthService: UserAuthService) {
    this.usernameUpdateForm = this.fb.group({
        newUsername: ['',
          [
            Validators.required,
            Validators.minLength(5),
            Validators.maxLength(20),
            Validators.pattern('^[a-zA-Z0-9_-]+$')
          ]
        ],
        repeatUsername: ['', [Validators.required]]
      },
      {validators: usernameMatchValidator()}
    );
  }

  userFetchErrorMsg?: string = 'Fetching profile';

  ngOnInit(): void {
    this.userProfileService.fetchUserProfile(this.userAuthService.getLoggedInUsername()).pipe(catchError(handleError)).subscribe({
      next: (fetchedData) => {
        this.userData = fetchedData;
        this.createForms();
        this.userFetchErrorMsg = undefined;
      },
      error: (error: string) => {
        this.userFetchErrorMsg = error;
      }
    })
  }

  createForms() {
    this.imageUpdateForm = this.fb.group({
      newImage: null
    });
    this.statusUpdateForm = this.fb.group({
      newStatus: [this.userData.status, Validators.maxLength(300)]
    });
    this.preferenceUpdateForm = this.fb.group({
      chosenPreference: [this.userData.preference, [Validators.required, Validators.min(0), Validators.max(preferenceArray.length - 1)]]
    });
  }

  imageUpdateForm!: FormGroup;

  get imageControl(): FormControl {
    return this.imageUpdateForm.get('newImage') as FormControl;
  }

  userData!: UserProfileData;
  statusUpdateForm!: FormGroup;
  usernameUpdateForm: FormGroup;
  preferenceUpdateForm!: FormGroup;


  profileImageUpdateErrorMessage?: String = undefined;

  updateProfileImage() {
    if (this.imageUpdateForm.pristine || this.imageUpdateForm.invalid) return;
    const formData = new FormData();
    formData.set('newImage', this.imageUpdateForm.value.newImage);
    formData.set('email', this.userData.email);
    this.profileImageUpdateErrorMessage = undefined;
    this.userProfileService.updateProfileImage(formData).subscribe({
      next: (response: UserProfileImageUpdateResponse) => {
        this.userData.profileImage = response.newProfileImage;
        this.imageUpdateForm.reset();
        this.userAuthService.updateUserMiniDataImage(response.newProfileImage);
      },
      error: (error: string) => {
        this.profileImageUpdateErrorMessage = error;
      }
    });
  }

  statusUpdateErrorMessage?: String = undefined;

  updateStatus() {
    if (this.statusUpdateForm.pristine || this.statusUpdateForm.invalid || this.statusUpdateForm.status === this.userData.status) return;
    const request: UserStatusUpdateRequest = {
      email: this.userData.email,
      status: this.statusUpdateForm.value.newStatus
    };
    this.statusUpdateErrorMessage = undefined;
    this.userProfileService.updateStatus(request).subscribe({
      next: (response) => {
        console.log(response);
        this.userData.status = response.newStatus;
        this.statusUpdateForm.reset();
      },
      error: (error: string) => {
        this.statusUpdateErrorMessage = error;
      }
    })
  }

  usernameUpdateErrorMessage?: String = undefined;

  get usernameMismatchError(): boolean {
    return this.usernameUpdateForm?.errors?.['usernameMismatch'] ?? false;
  }

  updateUsername() {
    if (this.usernameUpdateForm.pristine || this.usernameUpdateForm.invalid) return;
    if (this.usernameUpdateForm.value.newUsername === this.userData.username) {
      this.usernameUpdateErrorMessage = "New username is the same as the old one";
      return;
    }
    this.usernameUpdateErrorMessage = undefined;
    const request: UserUsernameUpdateRequest = {
      email: this.userData.email,
      username: this.usernameUpdateForm.value.newUsername
    };
    this.userProfileService.updateUsername(request).subscribe({
      next: (response: UserUsernameUpdateResponse) => {
        this.userData.username = response.newUsername;
        this.usernameUpdateForm.reset();
        this.userAuthService.updateUserMiniDataUsername(response.newUsername);
      },
      error: (error: string) => {
        this.usernameUpdateErrorMessage = error;
      }
    });
  }

  preferenceUpdateErrorMessage?: String = undefined;

  updatePreference() {
    const chosenPreference = this.preferenceUpdateForm.value.chosenPreference;
    if (chosenPreference % 10 < this.userData.rank) {
      this.preferenceUpdateErrorMessage = "You cannot choose this title at your current cultivation stage.";
      return;
    }
    this.preferenceUpdateErrorMessage = undefined;
    const request: UserPreferenceUpdateRequest = {email: this.userData.email, preference: chosenPreference};
    this.userProfileService.updatePreference(request).subscribe({
      next: (response: UserPreferenceUpdateResponse) => {
        this.userData.preference = response.newPreference;
        this.preferenceUpdateForm.reset();
      },
      error: (error: string) => {
        this.preferenceUpdateErrorMessage = error;
      }
    });
  }

  rankUpdateErrorMessage?: String = undefined;

  updateRank() {
    if (this.userData.currentScore < progressArray[this.userData.rank]) {
      this.rankUpdateErrorMessage = "You cannot attempt breakthrough yet.";
      return;
    }
    this.rankUpdateErrorMessage = undefined;
    this.userProfileService.updateRank(this.userData.email).subscribe({
      next: (response: UserRankUpdateResponse) => {
        this.userData.rank = response.newRank;
        this.userData.currentScore = response.newScore;
      },
      error: (error: string) => {
        this.rankUpdateErrorMessage = error;
      }
    });
  }


  protected readonly identifyByIndex = identifyByIndex;
  protected readonly preferenceArray = preferenceArray;
  protected readonly progressArray = progressArray;
  protected readonly rankArray = rankArray;
}
