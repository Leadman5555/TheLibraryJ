import {PageInfo} from './page-info';

export interface GenericPage<T> {
  content : T[];
  pageInfo : PageInfo;
}
