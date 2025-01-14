import {HttpErrorResponse} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';

export const handleError = (error: HttpErrorResponse): Observable<never> => {
  return throwError(() => {
    if (error.error instanceof ErrorEvent) {
      console.error('An error occurred:', error.error.message);
      return `An unexpected error occurred: ${error.error.message}`;
    } else {
      console.error(`Server returned status code ${error.status}, reason: ${error.error.error.message || 'Unknown'}`);
      return error.error.error.message || 'Unknown server error';
    }
  });
}

export const logError = (error: HttpErrorResponse): void => {
  if (error.error instanceof ErrorEvent) console.error('An error occurred:', error.error.message);
  else console.error(`Server returned status code ${error.status}, reason: ${error.error.error.message || 'Unknown'}`);
}
