import {BookPreview} from '../../../book/shared/models/book-preview';
import {PageInfo} from '../../../shared/paging/models/page-info';
import {GenericPage} from '../../../shared/paging/models/generic-page';

export interface BookPage extends GenericPage<BookPreview> {}
