import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {UserProfileData} from '../shared/user-profile-data';
import {ImageDropComponent} from '../../../shared/image-drop/image-drop.component';
import {NgForOf, NgIf} from '@angular/common';
import {usernameMatchValidator} from './usernameMatchValidator';
import {findTitle, preferenceArray, PreferenceTitle, progressArray, rankArray} from '../shared/rankTitles';
import {identifyByIndex} from '../../../shared/functions/indentify';
import {ProgressBarComponent} from '../../../shared/progress-bar/progress-bar.component';
import {catchError} from 'rxjs';
import {handleError} from '../../../shared/errorHandling/handleError';
import {UserProfileService} from '../user-profile.service';
import {UserAuthService} from '../../account/userAuth/user-auth.service';
import {
  UserPreferenceUpdateRequest,
  UserPreferenceUpdateResponse,
  UserProfileImageUpdateResponse,
  UserRankUpdateResponse,
  UserStatusUpdateRequest,
  UserUsernameUpdateRequest,
  UserUsernameUpdateResponse
} from './dto/UserUpdateDtos';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {parseDateString} from '../../../shared/functions/parseData';
import {carriageReturnLengthValidator} from '../../../shared/functions/carriageReturnLengthValidator';
import {imageFileTypeValidator} from '../../../shared/functions/fileTypeValidator';

const ANIMATION_IN_MS: number = 500;
const ANIMATION_OUT_MS: number = 1000;
const ANIMATION_HOLD_MS: number = 2000;

@Component({
  selector: 'app-user-profile-edit',
  imports: [
    ImageDropComponent,
    NgIf,
    NgForOf,
    ReactiveFormsModule,
    ProgressBarComponent,
  ],
  templateUrl: './user-profile-edit.component.html',
  styleUrl: './user-profile-edit.component.css',
  animations: [
    trigger('rankUpAnimation', [
      state('start', style({})),
      state('end', style({
        width: '300vw',
        height: '300vh',
        opacity: 1,
        zIndex: 1000,
      })),
      transition('start => end', [
        animate(`${ANIMATION_IN_MS}ms ease-in`)
      ]),
      transition('end => start', [
        animate(`${ANIMATION_OUT_MS}ms ease-in-out`)
      ])
    ])
  ]
})
export class UserProfileEditComponent implements OnInit {
  constructor(private fb: NonNullableFormBuilder, private userProfileService: UserProfileService, private userAuthService: UserAuthService) {
    this.usernameUpdateForm = this.fb.group({
        newUsername: ['',
          [
            Validators.required,
            Validators.minLength(5),
            Validators.maxLength(20),
            Validators.pattern('^(?=.*[a-zA-Z0-9]+)[a-zA-Z0-9_-]+$')
          ]
        ],
        repeatUsername: ['', [Validators.required]]
      },
      {validators: usernameMatchValidator()}
    );
  }

  ngOnInit(): void {
    const username = this.userAuthService.getLoggedInUsername();
    if(!username){
      window.location.replace('');
      return;
    }
    this.userProfileService.fetchUserProfile(username).pipe(catchError(handleError)).subscribe({
      next: (fetchedData) => {
        this.userData = fetchedData;
        this.createForms();
      },
      error: (_) => {
        this.userAuthService.sendLogOutEvent();
      }
    })
  }

  createForms() {
    this.imageUpdateForm = this.fb.group({
      newImage: [null, imageFileTypeValidator()],
    });
    this.statusUpdateForm = this.fb.group({
      newStatus: [this.userData.status, carriageReturnLengthValidator(0, 300)]
    });
    this.preferenceUpdateForm = this.fb.group({
      chosenPreference: [findTitle(this.userData.preference), [Validators.required, Validators.min(0), Validators.max(preferenceArray.length - 1)]]
    });
  }

  protected animationState: string = 'start';
  private isAnimating: boolean = false;

  private async triggerRankUpAnimation(modifier: number) {
    if (this.isAnimating) return;
    this.isAnimating = true;
    this.animationState = 'end';
    await this.delay(ANIMATION_IN_MS + modifier * ANIMATION_HOLD_MS);
    this.animationState = 'start';
    await this.delay(ANIMATION_OUT_MS);
    this.isAnimating = false;
  }

  private delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }


  protected imageUpdateForm!: FormGroup;

  get imageControl(): FormControl {
    return this.imageUpdateForm.get('newImage') as FormControl;
  }

  protected userData!: UserProfileData;
  protected statusUpdateForm!: FormGroup;
  protected usernameUpdateForm: FormGroup;
  protected preferenceUpdateForm!: FormGroup;


  profileImageUpdateErrorMessage?: String = undefined;

  updateProfileImage() {
    if (this.imageUpdateForm.invalid) return;
    const image = this.imageUpdateForm.value.newImage;
    if (image === this.userData.profileImage) return;
    const formData = new FormData();
    formData.set('newImage', image);
    formData.set('email', this.userData.email);
    this.profileImageUpdateErrorMessage = undefined;
    this.userProfileService.updateProfileImage(formData).subscribe({
      next: (response: UserProfileImageUpdateResponse) => {
        this.userData.profileImage = response.newProfileImage;
        this.imageUpdateForm.reset();
        this.userAuthService.updateUserMiniDataImage(response.newProfileImage);
      },
      error: (_) => {
        this.profileImageUpdateErrorMessage = 'Profile image update failed. Make sure you are sending a file in an an allowed format.';
      }
    });
  }

  protected statusUpdateErrorMessage?: String = undefined;
  protected statusUpdated: boolean = false;

  updateStatus() {
    this.statusUpdated = false;
    if (this.statusUpdateForm.pristine || this.statusUpdateForm.invalid || this.statusUpdateForm.status === this.userData.status) return;
    const request: UserStatusUpdateRequest = {
      email: this.userData.email,
      status: this.statusUpdateForm.value.newStatus
    };
    this.statusUpdateErrorMessage = undefined;
    this.userProfileService.updateStatus(request).subscribe({
      next: (response) => {
        this.statusUpdated = true;
        this.userData.status = response.newStatus;
        this.statusUpdateForm.reset({newStatus: response.newStatus});
      },
      error: (error: string) => {
        this.statusUpdateErrorMessage = error;
      }
    })
  }

  protected usernameUpdateErrorMessage?: String = undefined;

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

  protected preferenceUpdateErrorMessage?: String = undefined;

  updatePreference() {
    const chosenPreference: PreferenceTitle = this.preferenceUpdateForm.value.chosenPreference;
    if (chosenPreference.index === this.userData.preference) return;
    if (chosenPreference.requiredRank > this.userData.rank) {
      this.preferenceUpdateErrorMessage = "You cannot choose this title at your current cultivation stage.";
      return;
    }
    this.preferenceUpdateErrorMessage = undefined;
    const request: UserPreferenceUpdateRequest = {email: this.userData.email, preference: chosenPreference.index};
    this.userProfileService.updatePreference(request).subscribe({
      next: (response: UserPreferenceUpdateResponse) => {
        this.userData.preference = response.newPreference;
        this.preferenceUpdateForm.reset({chosenPreference: findTitle(response.newPreference)});
      },
      error: (error: string) => {
        this.preferenceUpdateErrorMessage = error;
      }
    });
  }

  protected rankUpdateErrorMessage?: String = undefined;

  updateRank() {
    if (this.userData.currentScore < progressArray[this.userData.rank]) {
      this.rankUpdateErrorMessage = "You cannot attempt breakthrough yet.";
      return;
    }
    this.rankUpdateErrorMessage = undefined;
    this.userProfileService.updateRank(this.userData.email).subscribe({
      next: (response: UserRankUpdateResponse) => {
        this.triggerRankUpAnimation(response.newRank);
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
  protected readonly parseDateString = parseDateString;
}
