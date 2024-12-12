import {Predicate} from '@angular/core';
import {BookPreview} from '../../shared/models/book-preview';
import {HttpParams} from '@angular/common/http';

function and(...predicates: ((value: BookPreview) => boolean)[]): (value: BookPreview) => boolean {
  return (value: BookPreview) => predicates.every((predicate) => predicate(value));
}

export class FormOutcome {
  private _predicate: Predicate<BookPreview>;
  private _params: HttpParams;
  private _isValid : boolean;
  private _isRedirected : boolean;
  private _sortAsc? : boolean;
  private _redirectMap: Record<string, any> = {};


  get isRedirected(): boolean {
    return this._isRedirected;
  }

  set isRedirected(value: boolean) {
    this._isRedirected = value;
  }

  public setRedirectTags(redirectTags: string[]) {
    this._redirectMap['hasTags'] = redirectTags;
    redirectTags.forEach(tag => this.appendParam('hasTags', tag));
  }

  public getRedirectTags(): string[] | undefined {
    return this._redirectMap['hasTags'];
  }

  public setRedirectTitleLike(titleLike: string) {
    this._redirectMap['titleLike'] = titleLike;
    this.appendParam('titleLike', titleLike);
  }

  public getRedirectTitleLike(): string | undefined {
    return this._redirectMap['titleLike'];
  }

  public setRedirectChapterCount(chapterCount: number) {
    this._redirectMap['minChapters'] = chapterCount;
    this.appendParam('minChapters', chapterCount);
  }

  public getRedirectChapterCount(): number | undefined {
    return this._redirectMap['chapterCount'];
  }

  public setRedirectMinRating(minRating: number) {
    this._redirectMap['minRating'] = minRating;
    this.appendParam('minRating', minRating);
  }

  public getRedirectMinRating(): number | undefined {
    return this._redirectMap['minRating'];
  }

  public setRedirectRatingOrder(ratingOrder: boolean) {
    this._redirectMap['ratingOrder'] = ratingOrder;
    this.appendParam('ratingOrder', ratingOrder);
  }

  public getRedirectRatingOrder(): boolean  | undefined {
    return this._redirectMap['ratingOrder'];
  }

  constructor(fetchNew : boolean) {
    this._isValid = !fetchNew;
    this._predicate = bp => true;
    this._params = new HttpParams();
    this._isRedirected = false;
  }



  get sortAsc(): boolean | undefined {
    return this._sortAsc;
  }

  set sortAsc(value: boolean) {
    this._sortAsc = value;
  }


  get isValid(): boolean {
    return this._isValid;
  }

  public setParam(key: string, value: any) {
    this._params = this._params?.set(key, value);
  }

  public appendParam(key: string, value: any) {
    this._params = this._params?.append(key, value);
  }

  public and(predicate: Predicate<BookPreview>) {
    this._predicate = and(this._predicate, predicate);
  }

  public setInvalid() {
    this._isValid = false;
  }

  get predicate(): Predicate<BookPreview> {
    return this._predicate;
  }

  get params(): HttpParams {
    return this._params;
  }
}
