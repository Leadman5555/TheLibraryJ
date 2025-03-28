export interface PagingHelper {

  onPreviousPage(): void;

  onNextPage(): void;

  onChosenPage(pageNumber: number): void;
}
