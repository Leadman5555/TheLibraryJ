import {Keyset} from './keyset';

export interface KeySetPage {
  firstResult: number;
  maxResults: number;
  lowest: Keyset;
  highest: Keyset;
  keysets: Keyset[];
}
