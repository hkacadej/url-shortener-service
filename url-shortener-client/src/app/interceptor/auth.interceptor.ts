import { HttpEvent, HttpHandlerFn, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable } from 'rxjs';
import { TokenService } from '../service/token/token.service';
import { ErrorService } from '../service/error/error.service';

export const authInterceptor: HttpInterceptorFn = (
  req: HttpRequest<any>,
  next: HttpHandlerFn
): Observable<HttpEvent<any>> => {

const tokenService = inject(TokenService);

const token = tokenService.getToken();


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
