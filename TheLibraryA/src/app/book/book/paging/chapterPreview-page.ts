import {ChapterPreview} from '../../shared/models/chapter-preview';
import {GenericPage} from '../../../shared/paging/models/generic-page';

export interface ChapterPreviewPage extends GenericPage<ChapterPreview> {
  bookId: string;
}
