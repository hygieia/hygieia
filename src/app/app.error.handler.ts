import { ErrorHandler, Injectable } from '@angular/core';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {

  constructor() { }

  handleError(error: Error) {
    const err = {
      message: error.message ? error.message : error.toString(),
      stack: error.stack ? error.stack : ''
    };
    if (error.message.includes('Error: Loading chunk')) {
      setTimeout(() => {
        window.location.reload();
      }, 100);
    }
    console.log(err);
  }
}
