import { Injectable, inject, NgZone, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { CurrentUserService } from './current-user.service';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class SessionTimeoutService {
  private currentUserService = inject(CurrentUserService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private zone = inject(NgZone);

  private timeoutMs = 30 * 60 * 1000;
  private warningMs = 2 * 60 * 1000;
  private timer: any;
  private warningTimer: any;
  private lastActivity = Date.now();
  private initialized = false;

  showWarning = new EventEmitter<void>();
  hideWarning = new EventEmitter<void>();

  get isWarningVisible(): boolean {
    return !!this.warningTimer;
  }

  start() {
    if (this.initialized) return;
    this.initialized = true;
    this.lastActivity = Date.now();

    this.zone.runOutsideAngular(() => {
      const events = ['mousedown', 'keydown', 'scroll', 'touchstart'];
      events.forEach(event => {
        document.addEventListener(event, () => this.resetTimer(), { passive: true });
      });
    });

    this.resetTimer();
  }

  private resetTimer() {
    this.lastActivity = Date.now();
    if (this.timer) clearTimeout(this.timer);
    if (this.warningTimer) {
      clearTimeout(this.warningTimer);
      this.warningTimer = null;
      this.hideWarning.emit();
    }

    this.timer = setTimeout(() => this.onTimeout(), this.timeoutMs);
    this.warningTimer = setTimeout(() => this.onWarning(), this.timeoutMs - this.warningMs);
  }

  private onWarning() {
    if (!this.currentUserService.isLoggedIn()) return;
    this.zone.run(() => {
      this.showWarning.emit();
    });
  }

  extendSession() {
    this.resetTimer();
  }

  private onTimeout() {
    if (!this.currentUserService.isLoggedIn()) return;
    this.zone.run(() => {
      this.authService.logout().subscribe({
        next: () => {
          this.router.navigate(['/login'], {
            queryParams: { sessionExpired: 'true' }
          });
        },
        error: () => {
          this.currentUserService.clearUser();
          this.router.navigate(['/login'], {
            queryParams: { sessionExpired: 'true' }
          });
        }
      });
    });
  }

  stop() {
    if (this.timer) clearTimeout(this.timer);
    if (this.warningTimer) clearTimeout(this.warningTimer);
    this.timer = null;
    this.warningTimer = null;
    this.initialized = false;
  }
}
