import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {
  constructor(private snackBar: MatSnackBar) { }

  showErrors(err: HttpErrorResponse | any): void{

    console.log(err)
    let errors:string[] = [];

    if (err.error && err.error.details && typeof err.error.details === 'string') {
      errors = [err.error.details];
    }else if (err.error && typeof err.error === 'object') {
      errors = Object.values(err.error);
    } else if (typeof err.error === 'string') {
      errors = [err.error];
    } else {
      errors = ['Something went Wrong'];
    }

    this.showErrorMessages(errors);
  }

  showErrorMessages(errors: string[]): void {
    const combined = errors.join(' \n ');
    this.snackBar.open(combined, 'Close', {
      duration: 8000,
      panelClass: ['error-snackbar'],
      horizontalPosition: 'center',
      verticalPosition: 'top',
    });
  }

}
