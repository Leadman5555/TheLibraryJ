import {Injectable} from '@angular/core';
import {UserMini} from '../../user/shared/models/user-mini';

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  constructor() { }

  public isLoggedIn(): boolean {
    return localStorage.getItem('access_token') !== null && localStorage.getItem('user_mini') !== null;
  }

  public getAccessToken(): string {
    return localStorage.getItem('access_token')!;
  }

  public setAccessToken(accessToken: string): void {
    localStorage.setItem('access_token', accessToken);
  }

  public setUserMini(userMini: UserMini): void {
    localStorage.setItem('user_mini', JSON.stringify(userMini));
  }

  public setUserMiniImage(image: string): boolean {
    const userMini = localStorage.getItem('user_mini');
    if(!userMini) return false;
    const parsedMini: UserMini = JSON.parse(userMini);
    parsedMini.profileImage = image;
    this.setUserMini(parsedMini);
    return true;
  }

  public setUserMiniUsername(username: string): boolean {
    const userMini = localStorage.getItem('user_mini');
    if(!userMini) return false;
    const parsedMini: UserMini = JSON.parse(userMini);
    parsedMini.username = username;
    this.setUserMini(parsedMini);
    return true;
  }

  public getUserMini(): UserMini | undefined {
    const fetched = localStorage.getItem('user_mini');
    if(fetched && fetched.length > 0) return JSON.parse(fetched);
    return undefined;
  }

  public getUserMiniUsername(): string | undefined {
    const fetched = localStorage.getItem('user_mini');
    if(fetched && fetched.length > 0) return JSON.parse(fetched).username;
    return undefined;
  }

  public saveData(key: string, value: string) {
    localStorage.setItem(key, value);
  }

  public getData(key: string) {
    return localStorage.getItem(key);
  }
  public removeData(key: string) {
    localStorage.removeItem(key);
  }

  public clearData() {
    localStorage.clear();
  }
}
