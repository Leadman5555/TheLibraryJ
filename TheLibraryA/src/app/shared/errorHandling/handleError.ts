import {HttpErrorResponse} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';

export const handleError = (error: HttpErrorResponse): Observable<never> => {
  return throwError(() => logAndExtractMessage(error));
}

export const logError = (error: HttpErrorResponse): void => {
  if (error.error instanceof ErrorEvent) console.error('An error occurred:', error.error.message);
  else {
    const subError = error.error;
    if (subError.errorDetails) {
      console.error(`Server returned status code ${error.status}, reason: ${subError.errorDetails.message || 'Unknown'}`);
    } else {
      console.error(`Failed to reach server.`);
    }
  }
}

export const logAndExtractMessage = (error: HttpErrorResponse): string => {
  if (error.error instanceof ErrorEvent) {
    console.error('An error occurred:', error.error.message);
    return `An unexpected error occurred: ${error.error.message}`;
  } else {
    const subError = error.error;
    if (subError.errorDetails) {
      console.error(`Server returned status code ${error.status}, reason: ${subError.errorDetails.message || 'Unknown'}`);
      return subError.errorDetails.message || 'Unknown server error';
    } else {
      console.log(error)
      console.error(`Request couldn't be completed. Status code: ${error.status}. Code starting with 4xx is client error.`);
      return `Request couldn't be completed. Status code: ${error.status}. Code starting with 4xx is client error.`;
    }
  }
}
