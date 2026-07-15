import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-api-tokens',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-header">
      <h2>API Tokens</h2>
      <p class="page-sub">Manage API tokens for external integrations and programmatic access</p>
    </div>

    <div class="coming-soon-card">
      <div class="coming-soon-icon">
        <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <path d="M21 2l-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0l3 3L22 7l-3-3m-3.5 3.5L19 4"/>
        </svg>
      </div>
      <h3>Coming Soon</h3>
      <p>API token management will allow administrators to create, revoke, and manage API tokens for external system integrations, webhook authentication, and programmatic access to the ERP platform.</p>
      <ul class="feature-list">
        <li>Generate Scoped API Tokens</li>
        <li>Token Expiration & Renewal</li>
        <li>Rate Limiting Per Token</li>
        <li>IP Whitelisting</li>
        <li>Token Usage Analytics</li>
        <li>Webhook Authentication</li>
      </ul>
      <button class="btn-disabled" disabled>Create API Token</button>
    </div>
  `,
  styles: [`
    .page-header { margin-bottom: 1.5rem; }
    .page-header h2 { margin: 0; font-size: 1.5rem; color: var(--text-primary); font-weight: 700; }
    .page-sub { margin: 2px 0 0; font-size: 0.875rem; color: var(--text-muted); }
    .coming-soon-card { background: var(--bg-secondary); border: 1px solid var(--border-color); border-radius: 16px; padding: 3rem; text-align: center; max-width: 600px; margin: 2rem auto; }
    .coming-soon-icon { width: 80px; height: 80px; border-radius: 50%; background: rgba(99, 102, 241, 0.1); display: flex; align-items: center; justify-content: center; margin: 0 auto 1.5rem; }
    .coming-soon-icon svg { color: var(--brand-color); }
    .coming-soon-card h3 { font-size: 1.5rem; font-weight: 700; color: var(--text-primary); margin: 0 0 0.75rem; }
    .coming-soon-card p { color: var(--text-muted); margin: 0 0 1.5rem; line-height: 1.6; }
    .feature-list { text-align: left; max-width: 400px; margin: 0 auto 2rem; }
    .feature-list li { padding: 0.5rem 0; color: var(--text-secondary); font-size: 0.875rem; display: flex; align-items: center; gap: 0.5rem; }
    .feature-list li::before { content: "\\2713"; color: var(--brand-color); font-weight: 700; }
    .btn-disabled { padding: 10px 24px; border: 1px solid var(--border-color); border-radius: 8px; background: var(--bg-tertiary); color: var(--text-muted); font-size: 0.875rem; font-weight: 500; cursor: not-allowed; opacity: 0.6; }
  `]
})
export class ApiTokensComponent {}
