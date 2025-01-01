import {UserProfile} from './user-profile';

export interface AuthUserData {
  userProfile? : UserProfile;
  token? : string;
}
