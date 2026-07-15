import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { CurrentUserService } from '../../services/current-user.service';

export const authGuard: CanActivateFn = (route, state) => {
  const currentUserService = inject(CurrentUserService);
  const router = inject(Router);

  if (currentUserService.isLoggedIn()) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};
