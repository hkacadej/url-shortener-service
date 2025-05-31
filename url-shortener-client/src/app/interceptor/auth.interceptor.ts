import { HttpEvent, HttpHandlerFn, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (
  req: HttpRequest<any>,
  next: HttpHandlerFn
): Observable<HttpEvent<any>> => {
const token = localStorage.getItem('jwt');

if (token) {
  const cloned = req.clone({
    setHeaders: {
      authorization: `Bearer ${token}`,
    },
  });
  return next(cloned);
} else {
  return next(req);
}
};
