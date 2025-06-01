import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenService } from '../service/token/token.service';
import { ErrorService } from '../service/error/error.service';

export const authGuard: CanActivateFn = (route, state) => {

  const router = inject(Router);
  const tokenService = inject(TokenService);
  const errorService = inject(ErrorService);


  if (tokenService.isTokenExpired()) {
    tokenService.clearToken();
    errorService.showErrorMessages(['Session is expired. Please Login'])
    router.navigate(['/login']);
    return false;
  }
  return true;

};
