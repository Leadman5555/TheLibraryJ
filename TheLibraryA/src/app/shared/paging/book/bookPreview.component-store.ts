import {GenericComponentStore} from '../generic.component-store';
import {BookPreview} from '../../../book/shared/models/book-preview';
import {BookPage} from './book-page';
import {inject} from '@angular/core';
import {BookService} from '../../../book/shared/book-service';

export abstract class BookPreviewComponentStore extends GenericComponentStore<BookPreview, BookPage> {
  protected readonly bookService = inject(BookService);

  protected constructor(initialState?: BookPage) {
    if(initialState) super(initialState);
    else{
      const defaultInitialState: BookPage = {
        content: [],
        pageInfo: {
          page: 0,
          totalPages: 0,
          keysetPage: {
            firstResult: 0,
            maxResults: 20,
            lowest: {
              number: 0,
              id: ''
            },
            highest: {
              number: 0,
              id: ''
            },
            keysets: []
          }
        }
      };
      super(defaultInitialState);
    }

  }

  protected override readonly updatePageNumber = this.updater(
    (state: BookPage, page: number) => ({
      ...state,
      pageInfo: {
        ...state.pageInfo,
        page: page
      }
    })
  );

}
