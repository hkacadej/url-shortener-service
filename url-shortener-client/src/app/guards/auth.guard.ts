import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {

      const token = localStorage.getItem('jwt');

    if (token) {
      // Optional: you could decode and validate token expiration here
      return true;
    }

    const router = inject(Router);
    return router.parseUrl('/login');

};
