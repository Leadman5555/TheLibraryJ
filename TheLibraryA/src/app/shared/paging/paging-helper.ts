export abstract class PagingHelper {

  abstract onPreviousPage(): void;

  abstract onNextPage(): void;

  abstract onChosenPage(pageNumber: number): void;

  identifyPage(index: number, page: number) {
    return page;
  }
}
