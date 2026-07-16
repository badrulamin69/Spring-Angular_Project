import { Component, Injectable } from '@angular/core';

export interface Toast {
  id: number;
  message: string;
  type: 'success' | 'error' | 'warning' | 'info';
}

@Component({
  selector: 'app-toast',
  standalone: true,
  template: '',
})
export class ToastComponent {}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private container: HTMLDivElement | null = null;
  private nextId = 0;

  private ensureContainer(): HTMLDivElement {
    if (!this.container) {
      this.container = document.createElement('div');
      this.container.style.cssText = 'position:fixed;top:80px;right:24px;z-index:2000;display:flex;flex-direction:column;gap:8px;';
      document.body.appendChild(this.container);
    }
    return this.container;
  }

  show(message: string, type: Toast['type'] = 'info') {
    const id = this.nextId++;
    const container = this.ensureContainer();

    const colors: Record<string, { bg: string; color: string; border: string; iconBg: string; icon: string }> = {
      success: { bg: '#f0fdf4', color: '#166534', border: '#bbf7d0', iconBg: '#dcfce7', icon: '\u2713' },
      error: { bg: '#fef2f2', color: '#dc2626', border: '#fecaca', iconBg: '#fee2e2', icon: '\u2717' },
      warning: { bg: '#fffbeb', color: '#92400e', border: '#fde68a', iconBg: '#fef3c7', icon: '!' },
      info: { bg: '#eff6ff', color: '#1d4ed8', border: '#bfdbfe', iconBg: '#dbeafe', icon: 'i' },
    };

    const c = colors[type] || colors['info'];

    const el = document.createElement('div');
    el.style.cssText = `display:flex;align-items:center;gap:10px;padding:12px 16px;border-radius:10px;box-shadow:0 8px 24px rgba(0,0,0,0.15);min-width:300px;max-width:420px;animation:toastSlideIn 0.3s ease-out;font-size:0.875rem;background:${c.bg};color:${c.color};border:1px solid ${c.border};`;

    el.innerHTML = `
      <span style="width:20px;height:20px;border-radius:50%;display:flex;align-items:center;justify-content:center;font-size:11px;font-weight:700;flex-shrink:0;background:${c.iconBg};">${c.icon}</span>
      <span style="flex:1;">${message}</span>
      <button style="background:none;border:none;font-size:1.25rem;cursor:pointer;color:inherit;opacity:0.5;padding:0;">&times;</button>
    `;

    el.querySelector('button')!.onclick = () => {
      el.style.opacity = '0';
      el.style.transform = 'translateX(40px)';
      el.style.transition = 'all 0.3s ease-out';
      setTimeout(() => el.remove(), 300);
    };

    container.appendChild(el);
    setTimeout(() => {
      el.style.opacity = '0';
      el.style.transform = 'translateX(40px)';
      el.style.transition = 'all 0.3s ease-out';
      setTimeout(() => el.remove(), 300);
    }, 4000);
  }

  success(msg: string) { this.show(msg, 'success'); }
  error(msg: string) { this.show(msg, 'error'); }
  warning(msg: string) { this.show(msg, 'warning'); }
  info(msg: string) { this.show(msg, 'info'); }
}
