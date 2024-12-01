import {Pipe, PipeTransform} from '@angular/core';
import {BookPreview} from './book-preview';
import {BookTag} from './BookTag';

@Pipe({
  name: 'filterByTagAndName'
})
export class FilterByTagNamePipe implements PipeTransform {

  transform(bookPreviews: BookPreview[], activeTags: BookTag[], name: string, minChapterCount: number, order: string): BookPreview[] {
    console.log("le" + bookPreviews.length);
    console.log(name);
    console.log(minChapterCount);
    console.log(order);
    console.log(activeTags.length);
    if (!bookPreviews) return [];
    console.log("sec");
    if(name.length === 0 && minChapterCount === 0 && activeTags.length === 0) return FilterByTagNamePipe.sortPreviews(bookPreviews, order);
    const noTags : boolean = activeTags.length === 0;
    console.log("th" + noTags);
    const filtered = bookPreviews.filter(preview => {
      if (preview.title.includes(name) && preview.chapterCount >= minChapterCount) {
        if(!noTags) return FilterByTagNamePipe.containsTags(preview.bookTags, activeTags)
        return true;
      } else return false;
    });
    return FilterByTagNamePipe.sortPreviews(filtered, name);
  }

  private static sortPreviews(bookPreviews: BookPreview[], order: string): BookPreview[] {
    if (order === 'Ascending') return bookPreviews.sort((a, b) => {
      if (a.averageRating < b.averageRating) {
        return -1;
      } else if (a.averageRating > b.averageRating) {
        return 1;
      } else {
        return 0;
      }
    });
    else if (order === 'Descending') return bookPreviews.sort((a, b) => {
        if (a.averageRating < b.averageRating) {
          return 1;
        } else if (a.averageRating > b.averageRating) {
          return -1;
        } else {
          return 0;
        }
    });
    return bookPreviews;
  }

  private static containsTags(pretenderTags: BookTag[], activeTags: BookTag[]): boolean {
    for (let i = 0; i < activeTags.length; i++) if (!pretenderTags.includes(activeTags[i])) return false;
    return true;
  }
}
