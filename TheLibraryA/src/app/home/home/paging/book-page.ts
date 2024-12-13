import {BookPreview} from '../../../book/shared/models/book-preview';
import {PageInfo} from './page-info';

export interface BookPage {
  content : BookPreview[];
  pageInfo : PageInfo
}
