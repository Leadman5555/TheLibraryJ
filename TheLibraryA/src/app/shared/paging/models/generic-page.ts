import {PageInfo} from './page-info';

export interface GenericPage<TS> {
  content : TS[];
  pageInfo : PageInfo;
}
