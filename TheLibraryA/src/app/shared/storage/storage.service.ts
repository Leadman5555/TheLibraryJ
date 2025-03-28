import {Injectable} from '@angular/core';
import {UserMini} from '@app/user/shared/models/user-mini';

const ACCESS_TOKEN_KEY = 'access_token';
const USER_MINI_KEY = 'user_mini';
const DEVICE_FAV_BOOKS_KEY = 'device_fav_books';
const LOGGED_FAV_BOOKS_KEY = 'logged_fav_books';
const LOGGED_SUB_BOOKS_KEY = 'logged_sub_books';

const LOGGED_KEYS: string[] = [ACCESS_TOKEN_KEY, USER_MINI_KEY, LOGGED_FAV_BOOKS_KEY, LOGGED_SUB_BOOKS_KEY];

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  constructor() { }

  public isLoggedIn(): boolean {
    return localStorage.getItem(ACCESS_TOKEN_KEY) !== null && localStorage.getItem(USER_MINI_KEY) !== null;
  }

  public getAccessToken(): string {
    return localStorage.getItem(ACCESS_TOKEN_KEY)!;
  }

  public setAccessToken(accessToken: string): void {
    localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
  }

  public setUserMini(userMini: UserMini): void {
    localStorage.setItem(USER_MINI_KEY, JSON.stringify(userMini));
  }

  public setUserMiniImage(image: string): boolean {
    const userMini = localStorage.getItem(USER_MINI_KEY);
    if(!userMini) return false;
    const parsedMini: UserMini = JSON.parse(userMini);
    parsedMini.profileImage = image;
    this.setUserMini(parsedMini);
    return true;
  }

  public setUserMiniUsername(username: string): boolean {
    const userMini = localStorage.getItem(USER_MINI_KEY);
    if(!userMini) return false;
    const parsedMini: UserMini = JSON.parse(userMini);
    parsedMini.username = username;
    this.setUserMini(parsedMini);
    return true;
  }

  public getUserMini(): UserMini | undefined {
    const fetched = localStorage.getItem(USER_MINI_KEY);
    if(fetched && fetched.length > 0) return JSON.parse(fetched);
    return undefined;
  }

  public getUserMiniUsername(): string | null {
    const fetched = localStorage.getItem(USER_MINI_KEY);
    if(fetched && fetched.length > 0) return JSON.parse(fetched).username;
    return null;
  }

  public getUserMiniEmail(): string | null {
    const fetched = localStorage.getItem(USER_MINI_KEY);
    if(fetched && fetched.length > 0) return JSON.parse(fetched).email;
    return null;
  }

  public setLoggedFavBooks(favBooksIds: string[]): void {
    localStorage.setItem(LOGGED_FAV_BOOKS_KEY, JSON.stringify(favBooksIds));
  }

  public getLoggedFavBooks(): string[] | null {
    const fetched = localStorage.getItem(LOGGED_FAV_BOOKS_KEY);
    if(fetched && fetched.length > 0) return JSON.parse(fetched);
    return null;
  }

  public isBookInLoggedFavBooks(bookId: string): boolean {
    const favBooks = this.getLoggedFavBooks();
    if(!favBooks) return false;
    return favBooks.includes(bookId);
  }

  public addBookToLoggedFavBooks(bookId: string, desiredCount: number): boolean {
    const favBooks = this.getLoggedFavBooks();
    if(!favBooks || favBooks.length === 0){
      if(desiredCount === 0){
        this.setLoggedFavBooks([bookId]);
        return true;
      }
      return false;
    }
    if(!favBooks.includes(bookId)) favBooks.push(bookId);
    if(favBooks.length !== desiredCount) return false;
    this.setLoggedFavBooks(favBooks);
    return true;
  }

  public removeBookFromLoggedFavBooks(bookId: string): void {
    const favBooks = this.getLoggedFavBooks();
    if(!favBooks) return;
    this.setLoggedFavBooks(favBooks.filter(id => id !== bookId));
  }

  public setDeviceFavBooks(favBooksIds: string[]): void {
    localStorage.setItem(DEVICE_FAV_BOOKS_KEY, JSON.stringify(favBooksIds));
  }

  public getDeviceFavBooks(): string[] | null {
    const fetched = localStorage.getItem(DEVICE_FAV_BOOKS_KEY);
    if(fetched && fetched.length > 0) return JSON.parse(fetched);
    return null;
  }

  public isBookInDeviceFavBooks(bookId: string): boolean {
    const favBooks = this.getDeviceFavBooks();
    if(!favBooks) return false;
    return favBooks.includes(bookId);
  }

  public addBookToDeviceFavBooks(bookId: string): number {
    const favBooks = this.getDeviceFavBooks();
    if(!favBooks){
      this.setDeviceFavBooks([bookId]);
      return 1;
    }
    if(!favBooks.includes(bookId)) favBooks.push(bookId);
    this.setDeviceFavBooks(favBooks);
    return favBooks.length;
  }

  public removeBookFromDeviceFavBooks(bookId: string): number {
    let favBooks = this.getDeviceFavBooks();
    if(!favBooks) return 0;
    favBooks = favBooks.filter(id => id !== bookId);
    this.setDeviceFavBooks(favBooks);
    return favBooks.length;
  }

  public setLoggedSubBooks(subBooksIds: string[]): void {
    localStorage.setItem(LOGGED_SUB_BOOKS_KEY, JSON.stringify(subBooksIds));
  }

  public getLoggedSubBooks(): string[] | null {
    const fetched = localStorage.getItem(LOGGED_SUB_BOOKS_KEY);
    if(fetched && fetched.length > 0) return JSON.parse(fetched);
    return null;
  }

  public isBookInLoggedSubBooks(bookId: string): boolean {
    const subBooks = this.getLoggedSubBooks();
    if(!subBooks) return false;
    return subBooks.includes(bookId);
  }

  public addBookToLoggedSubBooks(bookId: string, desiredCount: number): boolean {
    const subBooks = this.getLoggedSubBooks();
    if(!subBooks || subBooks.length === 0){
      if(desiredCount === 0){
        this.setLoggedSubBooks([bookId]);
        return true;
      }
      return false;
    }
    if(!subBooks.includes(bookId)) subBooks.push(bookId);
    if(subBooks.length !== desiredCount) return false;
    this.setLoggedSubBooks(subBooks);
    return true;
  }

  public removeBookFromLoggedSubBooks(bookId: string): void {
    const subBooks = this.getLoggedSubBooks();
    if(!subBooks) return;
    this.setLoggedSubBooks(subBooks.filter(id => id !== bookId));
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
    LOGGED_KEYS.forEach(key => localStorage.removeItem(key));
    sessionStorage.clear();
  }
}
