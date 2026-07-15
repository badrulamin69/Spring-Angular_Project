import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { CurrentUserService } from '../../services/current-user.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const currentUser = inject(CurrentUserService);
  const router = inject(Router);
  
  const requiredRoles = route.data?.['roles'] as string[] | undefined;
  if (!requiredRoles || requiredRoles.length === 0) return true;
  
  const userRole = currentUser.roleCode();
  if (userRole && requiredRoles.includes(userRole)) return true;
  
  router.navigate(['/access-denied']);
  return false;
};
