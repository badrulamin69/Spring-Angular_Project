import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { CurrentUserService } from '../../services/current-user.service';

export const permissionGuard: CanActivateFn = (route, state) => {
  const currentUserService = inject(CurrentUserService);
  const router = inject(Router);

  const requiredPermission = route.data['permission'] as string;

  if (!requiredPermission || currentUserService.hasPermission(requiredPermission)) {
    return true;
  }

  router.navigate(['/access-denied']);
  return false;
};
