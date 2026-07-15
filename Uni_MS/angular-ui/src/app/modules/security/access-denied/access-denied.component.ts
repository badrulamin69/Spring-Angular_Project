import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-access-denied',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="denied-container">
      <div class="denied-card">
        <span class="denied-icon">≡ƒÜ½</span>
        <h1>Access Denied</h1>
        <p>You do not have the required permissions to view this page.</p>
        <a routerLink="/" class="btn btn-primary">Go to Home</a>
      </div>
    </div>
  `,
  styles: [`
    .denied-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: calc(100vh - 120px);
      font-family: 'Segoe UI', sans-serif;
    }
    .denied-card {
      text-align: center;
      padding: 40px;
      background: white;
      border-radius: 12px;
      box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1);
      max-width: 400px;
    }
    .denied-icon {
      font-size: 64px;
      display: block;
      margin-bottom: 20px;
    }
    h1 {
      font-size: 24px;
      color: #1e293b;
      margin-bottom: 12px;
    }
    p {
      color: #64748b;
      margin-bottom: 24px;
    }
    .btn {
      display: inline-block;
      padding: 10px 20px;
      background: #6366f1;
      color: white;
      text-decoration: none;
      border-radius: 6px;
      font-weight: 600;
    }
  `]
})
export class AccessDeniedComponent {}
