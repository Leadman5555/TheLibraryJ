import {HttpErrorResponse} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';

export const handleError = (error: HttpErrorResponse): Observable<never> => {
  return throwError(() => {
    if (error.error instanceof ErrorEvent) {
      console.error('An error occurred:', error.error.message);
      return `An unexpected error occurred: ${error.error.message}`;
    } else {
      const subError = error.error;
      if(subError.errorDetails){
        console.error(`Server returned status code ${error.status}, reason: ${subError.errorDetails.message || 'Unknown'}`);
        return subError.errorDetails.message || 'Unknown server error';
      }else {
        console.error(`Failed to reach server.`);
        return 'Failed to reach server. Cannot load resource.';
      }
    }
  });
}

export const logError = (error: HttpErrorResponse): void => {
  if (error.error instanceof ErrorEvent) console.error('An error occurred:', error.error.message);
  else console.error(`Server returned status code ${error.status}, reason: ${error.error.error.message || 'Unknown'}`);
}
