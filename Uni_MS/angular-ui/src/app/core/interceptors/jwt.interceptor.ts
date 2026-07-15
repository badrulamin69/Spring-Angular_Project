import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError, Subject, take, Observable, from } from 'rxjs';
import { TokenService } from '../../services/token.service';
import { AuthService } from '../../services/auth.service';

let isRefreshing = false;
const refreshCompleted$ = new Subject<string | null>();

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenService = inject(TokenService);
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = tokenService.getToken();

  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && token && !req.url.includes('/auth/')) {
        if (!isRefreshing) {
          isRefreshing = true;
          return authService.refreshToken().pipe(
            switchMap(response => {
              const newToken = response?.data?.token || response?.token;
              isRefreshing = false;
              refreshCompleted$.next(newToken || null);
              if (newToken) {
                const newReq = req.clone({
                  setHeaders: {
                    Authorization: `Bearer ${newToken}`
                  }
                });
                return next(newReq);
              }
              tokenService.signOut();
              router.navigate(['/login']);
              return throwError(() => error);
            }),
            catchError(err => {
              isRefreshing = false;
              refreshCompleted$.next(null);
              tokenService.signOut();
              router.navigate(['/login']);
              return throwError(() => err);
            })
          );
        } else {
          return new Observable<any>(subscriber => {
            refreshCompleted$.pipe(take(1)).subscribe({
              next: (newToken) => {
                if (newToken) {
                  const newReq = req.clone({
                    setHeaders: { Authorization: `Bearer ${newToken}` }
                  });
                  next(newReq).subscribe({
                    next: res => { subscriber.next(res); subscriber.complete(); },
                    error: err => { subscriber.error(err); }
                  });
                } else {
                  tokenService.signOut();
                  router.navigate(['/login']);
                  subscriber.error(error);
                }
              }
            });
          });
        }
      }

      if (error.status === 403) {
        if (!req.url.includes('/auth/')) {
          router.navigate(['/403']);
        }
      }

      return throwError(() => error);
    })
  );
};
