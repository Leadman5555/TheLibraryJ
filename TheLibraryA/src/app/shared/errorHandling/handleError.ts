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
    console.error('A client error occurred:', error.error.message);
    return `An unexpected client error occurred: ${error.error.message}`;
  } else {
    const subError = error.error;
    if (subError.errorDetails) {
      console.error(`Server returned status code ${error.status}, reason: ${subError.errorDetails.message || 'Unknown'}`);
      return subError.errorDetails.message || 'Unknown server error';
    } else {
      console.log(error)
      if(error.status === 400) return "Request data invalid - make sure all fields are filled in correctly."
      const err = `Request failed. Status code: ${error.status}. Code starting with 4xx is client error.`;
      console.error(err);
      return err;
    }
  }
}
