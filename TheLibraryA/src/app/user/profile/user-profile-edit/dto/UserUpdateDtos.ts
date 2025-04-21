export interface UserPreferenceUpdateResponse {
  newPreference: number;
}

export interface UserPreferenceUpdateRequest {
  email: string;
  preference: number;
}

export interface UserProfileImageUpdateResponse {
  newProfileImageUrl: string;
}

export interface UserRankUpdateResponse {
  newRank: number;
  newScore: number;
  newPreference: number;
}

export interface UserStatusUpdateResponse {
  newStatus: string;
}

export interface UserStatusUpdateRequest {
  email: string;
  status: string;
}

export interface UserUsernameUpdateResponse {
  newUsername: string;
  dateUpdatedAt: string;
}

export interface UserUsernameUpdateRequest {
  email: string;
  username: string;
}
