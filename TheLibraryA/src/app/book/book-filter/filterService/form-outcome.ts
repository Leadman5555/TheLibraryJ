import {Predicate} from '@angular/core';
import {BookPreview} from '../../book-preview';
import {HttpParams} from '@angular/common/http';

function and(...predicates: ((value: BookPreview) => boolean)[]): (value: BookPreview) => boolean {
  return (value: BookPreview) => predicates.every((predicate) => predicate(value));
}

export class FormOutcome {
  private _predicate: Predicate<BookPreview>;
  private _params: HttpParams;
  private _isValid : boolean;
  private _sortAsc? : boolean;

  constructor(fetchNew : boolean) {
    this._isValid = !fetchNew;
    this._predicate = bp => true;
    this._params = new HttpParams();
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
