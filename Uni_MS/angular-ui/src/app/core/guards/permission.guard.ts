import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { CurrentUserService } from '../../services/current-user.service';
import { AuthService } from '../../services/auth.service';
import { map, catchError, of } from 'rxjs';

export const permissionGuard: CanActivateFn = (route, state) => {
  const currentUserService = inject(CurrentUserService);
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!currentUserService.isLoggedIn()) {
    router.navigate(['/login']);
    return false;
  }

  const requiredPermission = route.data['permission'] as string;

  if (!requiredPermission) {
    return true;
  }

  if (currentUserService.hasPermission(requiredPermission)) {
    return true;
  }

  return authService.getMe().pipe(
    map(() => {
      if (currentUserService.hasPermission(requiredPermission)) {
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
