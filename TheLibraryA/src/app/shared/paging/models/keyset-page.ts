import {Keyset} from './keyset';

export interface KeysetPage {
  firstResult: number;
  maxResults: number;
  lowest: Keyset;
  highest: Keyset;
  keysets: Keyset[];
}
