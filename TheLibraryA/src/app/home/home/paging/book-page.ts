import {BookPreview} from '../../../book/book-preview';
import {PageInfo} from './page-info';

export interface BookPage {
  content : BookPreview[];
  pageInfo : PageInfo
}
