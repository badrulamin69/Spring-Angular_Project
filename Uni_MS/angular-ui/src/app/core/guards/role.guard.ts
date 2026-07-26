import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { CurrentUserService } from '../../services/current-user.service';
import { AuthService } from '../../services/auth.service';
import { map, catchError, of } from 'rxjs';

export const roleGuard: CanActivateFn = (route, state) => {
  const currentUser = inject(CurrentUserService);
  const authService = inject(AuthService);
  const router = inject(Router);
  
  const requiredRoles = route.data?.['roles'] as string[] | undefined;
  if (!requiredRoles || requiredRoles.length === 0) return true;
  
  const userRole = currentUser.roleCode();
  if (userRole && requiredRoles.includes(userRole)) return true;

  return authService.getMe().pipe(
    map(() => {
      const updatedRole = currentUser.roleCode();
      if (updatedRole && requiredRoles.includes(updatedRole)) {
        return true;
      }
      router.navigate(['/access-denied']);
      return false;
    }),
    catchError(() => {
      router.navigate(['/access-denied']);
      return of(false);
    })
  );
};
