import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {catchError, Observable} from 'rxjs';
import {UserProfileData} from './shared/user-profile-data';
import {handleError} from '../../shared/errorHandling/handleError';
import {
  UserPreferenceUpdateRequest,
  UserPreferenceUpdateResponse,
  UserProfileImageUpdateResponse,
  UserRankUpdateResponse, UserStatusUpdateRequest,
  UserStatusUpdateResponse, UserUsernameUpdateRequest,
  UserUsernameUpdateResponse
} from './user-profile-edit/dto/UserUpdateDtos';

@Injectable({
  providedIn: 'root'
})
export class UserProfileService {

  private readonly baseUrl: string = 'http://localhost:8082/v0.9/na/user/';
  private readonly baseAuthUrl: string ='http://localhost:8082/v0.9/user/';

  constructor(private http: HttpClient) { }

  fetchUserProfile(forUsername: string): Observable<UserProfileData> {
    return this.http.get<UserProfileData>(this.baseUrl + forUsername);
  }

  updateRank(forEmail: string) : Observable<UserRankUpdateResponse> {
    return this.http.patch<UserRankUpdateResponse>(this.baseAuthUrl + 'profile/rank/' + forEmail, null)
      .pipe(catchError(handleError));
  }

  updateUsername(updateRequest: UserUsernameUpdateRequest) : Observable<UserUsernameUpdateResponse> {
    return this.http.patch<UserUsernameUpdateResponse>(this.baseAuthUrl + 'profile/username', updateRequest)
      .pipe(catchError(handleError));
  }

  updateStatus(updateRequest: UserStatusUpdateRequest) : Observable<UserStatusUpdateResponse> {
    return this.http.patch<UserStatusUpdateResponse>(this.baseAuthUrl + 'profile/status', updateRequest)
      .pipe(catchError(handleError));
  }

  updateProfileImage(profileImageUpdateRequest: FormData) : Observable<UserProfileImageUpdateResponse> {
    return this.http.patch<UserProfileImageUpdateResponse>(this.baseAuthUrl + 'profile/image', profileImageUpdateRequest)
      .pipe(catchError(handleError));
  }

  updatePreference(preferenceUpdateRequest: UserPreferenceUpdateRequest) : Observable<UserPreferenceUpdateResponse> {
    return this.http.patch<UserPreferenceUpdateResponse>(this.baseAuthUrl + 'profile/preference', preferenceUpdateRequest)
      .pipe(catchError(handleError));
  }
}
