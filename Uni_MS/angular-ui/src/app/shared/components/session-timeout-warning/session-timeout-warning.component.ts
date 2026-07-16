import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { SessionTimeoutService } from '../../../services/session-timeout.service';

@Component({
  selector: 'app-session-timeout-warning',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="session-warning-overlay" *ngIf="visible">
      <div class="session-warning-modal">
        <div class="warning-icon">⚠</div>
        <h3>Session Expiring Soon</h3>
        <p>Your session will expire in <strong>{{ countdown }}</strong> seconds due to inactivity.</p>
        <div class="warning-actions">
          <button class="btn btn-primary" (click)="extendSession()">Stay Logged In</button>
          <button class="btn btn-secondary" (click)="logout()">Logout</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .session-warning-overlay {
      position: fixed;
      top: 0; left: 0; right: 0; bottom: 0;
      background: rgba(0,0,0,0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 10000;
    }
    .session-warning-modal {
      background: white;
      border-radius: 12px;
      padding: 32px;
      text-align: center;
      max-width: 380px;
      box-shadow: 0 20px 60px rgba(0,0,0,0.3);
    }
    .warning-icon { font-size: 48px; margin-bottom: 16px; }
    h3 { font-size: 18px; color: #1e293b; margin: 0 0 8px; }
    p { color: #64748b; margin: 0 0 24px; font-size: 14px; }
    strong { color: #f59e0b; font-size: 18px; }
    .warning-actions { display: flex; gap: 12px; justify-content: center; }
    .btn {
      padding: 10px 20px;
      border-radius: 6px;
      font-weight: 600;
      font-size: 14px;
      cursor: pointer;
      border: none;
    }
    .btn-primary { background: #6366f1; color: white; }
    .btn-secondary { background: #e2e8f0; color: #475569; }
  `]
})
export class SessionTimeoutWarningComponent implements OnInit, OnDestroy {
  private sessionService = inject(SessionTimeoutService);
  private router = inject(Router);
  private sub?: Subscription;

  visible = false;
  countdown = 120;
  private countdownInterval: any;

  ngOnInit() {
    this.sub = this.sessionService.showWarning.subscribe(() => {
      this.visible = true;
      this.startCountdown();
    });
  }

  private startCountdown() {
    this.countdown = 120;
    if (this.countdownInterval) clearInterval(this.countdownInterval);
    this.countdownInterval = setInterval(() => {
      this.countdown--;
      if (this.countdown <= 0) {
        clearInterval(this.countdownInterval);
        this.visible = false;
      }
    }, 1000);
  }

  extendSession() {
    this.sessionService.extendSession();
    this.visible = false;
    if (this.countdownInterval) clearInterval(this.countdownInterval);
  }

  logout() {
    this.sessionService.stop();
    this.visible = false;
    if (this.countdownInterval) clearInterval(this.countdownInterval);
    this.router.navigate(['/login']);
  }

  ngOnDestroy() {
    this.sub?.unsubscribe();
    if (this.countdownInterval) clearInterval(this.countdownInterval);
  }
}
