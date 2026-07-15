import { Component, OnInit, Injectable } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface Toast {
  id: number;
  message: string;
  type: 'success' | 'error' | 'warning' | 'info';
}

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toast-container">
      @for (toast of toasts; track toast.id) {
        <div class="toast" [class]="'toast-' + toast.type">
          <span class="toast-icon">
            @switch (toast.type) {
              @case ('success') { Γ£ô }
              @case ('error') { Γ£ò }
              @case ('warning') { ! }
              @case ('info') { i }
            }
          </span>
          <span class="toast-msg">{{ toast.message }}</span>
          <button class="toast-close" (click)="remove(toast.id)">&times;</button>
        </div>
      }
    </div>
  `,
  styles: [`
    .toast-container { position: fixed; top: 80px; right: 24px; z-index: 2000; display: flex; flex-direction: column; gap: 8px; }
    .toast { display: flex; align-items: center; gap: 10px; padding: 12px 16px; border-radius: 10px; box-shadow: 0 8px 24px rgba(0,0,0,0.15); min-width: 300px; max-width: 420px; animation: slideIn 0.3s ease-out; font-size: 0.875rem; }
    .toast-success { background: #f0fdf4; color: #166534; border: 1px solid #bbf7d0; }
    .toast-error { background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; }
    .toast-warning { background: #fffbeb; color: #92400e; border: 1px solid #fde68a; }
    .toast-info { background: #eff6ff; color: #1d4ed8; border: 1px solid #bfdbfe; }
    .toast-icon { width: 20px; height: 20px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 11px; font-weight: 700; flex-shrink: 0; }
    .toast-success .toast-icon { background: #dcfce7; }
    .toast-error .toast-icon { background: #fee2e2; }
    .toast-warning .toast-icon { background: #fef3c7; }
    .toast-info .toast-icon { background: #dbeafe; }
    .toast-msg { flex: 1; }
    .toast-close { background: none; border: none; font-size: 1.25rem; cursor: pointer; color: inherit; opacity: 0.5; padding: 0; }
    .toast-close:hover { opacity: 1; }
    @keyframes slideIn { from { opacity: 0; transform: translateX(40px); } to { opacity: 1; transform: translateX(0); } }
  `]
})
export class ToastComponent {
  toasts: Toast[] = [];
  private nextId = 0;

  show(message: string, type: Toast['type'] = 'info') {
    const id = this.nextId++;
    this.toasts.push({ id, message, type });
    setTimeout(() => this.remove(id), 4000);
  }

  remove(id: number) {
    this.toasts = this.toasts.filter(t => t.id !== id);
  }
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private component: ToastComponent | null = null;

  setComponent(c: ToastComponent) { this.component = c; }
  success(msg: string) { this.component?.show(msg, 'success'); }
  error(msg: string) { this.component?.show(msg, 'error'); }
  warning(msg: string) { this.component?.show(msg, 'warning'); }
  info(msg: string) { this.component?.show(msg, 'info'); }
}
