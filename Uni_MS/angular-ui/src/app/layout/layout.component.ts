import { Component, OnInit, inject } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MenuService, MenuItem } from '../services/menu.service';
import { CurrentUserService } from '../services/current-user.service';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';
import { SessionTimeoutService } from '../services/session-timeout.service';
import { SessionTimeoutWarningComponent } from '../shared/components/session-timeout-warning/session-timeout-warning.component';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule, SessionTimeoutWarningComponent],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss'
})
export class LayoutComponent implements OnInit {
  isSidebarCollapsed = false;
  isMobileMenuOpen = false;
  modules: MenuItem[] = [];
  expandedModules: Record<number, boolean> = {};
  showUserMenu = false;

  private menuService = inject(MenuService);
  private currentUserService = inject(CurrentUserService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private notificationService = inject(NotificationService);
  private sessionTimeoutService = inject(SessionTimeoutService);

  unreadNotificationCount = 0;

  moduleColors: Record<string, string> = {
    'Security': '#1b3a5f',
    'Academic': '#002d5f',
    'Admissions': '#e6a817',
    'Students': '#0ea5e9',
    'Teachers': '#5a3e8e',
    'Administration': '#002d5f',
    'HRM': '#dc3545',
    'Examination': '#c8102e',
    'LMS': '#3388cc',
    'Finance': '#f97316',
    'Library': '#17a2b8',
    'Hostel': '#84cc16',
    'Transport': '#5a3e8e',
    'Communication': '#0284c7',
    'Activities': '#e11d48',
    'Reports': '#64748b',
    'Settings': '#475569',
    'System': '#1b3a5f',
    'Dashboard': '#002d5f'
  };

  isDarkMode = false;

  ngOnInit() {
    const savedTheme = localStorage.getItem('theme');
    this.isDarkMode = savedTheme === 'dark';
    this.applyTheme();
    this.menuService.menus$.subscribe(menus => {
      this.modules = menus;
    });
    this.menuService.getMyMenus().subscribe();
    this.notificationService.getUnreadCount().subscribe();
    this.notificationService.unreadCount$.subscribe(count => {
      this.unreadNotificationCount = count;
    });
    this.sessionTimeoutService.start();
  }

  toggleSidebar() {
    if (window.innerWidth <= 1024) {
      this.isMobileMenuOpen = !this.isMobileMenuOpen;
    } else {
      this.isSidebarCollapsed = !this.isSidebarCollapsed;
    }
  }

  closeMobileMenu() {
    this.isMobileMenuOpen = false;
  }

  toggleModule(moduleId: number) {
    this.expandedModules[moduleId] = !this.expandedModules[moduleId];
  }

  isModuleExpanded(moduleId: number): boolean {
    return !!this.expandedModules[moduleId];
  }

  getModuleColor(module: MenuItem): string {
    return this.moduleColors[module.title] || '#002d5f';
  }

  getModuleInitial(module: MenuItem): string {
    return module.title ? module.title.charAt(0).toUpperCase() : '?';
  }

  toggleUserMenu() {
    this.showUserMenu = !this.showUserMenu;
  }

  logout() {
    this.showUserMenu = false;
    this.sessionTimeoutService.stop();
    this.menuService.clearMenus();
    this.authService.logout().subscribe({
      next: () => this.router.navigate(['/login']),
      error: () => {
        this.currentUserService.clearUser();
        this.router.navigate(['/login']);
      }
    });
  }

  toggleTheme() {
    this.isDarkMode = !this.isDarkMode;
    localStorage.setItem('theme', this.isDarkMode ? 'dark' : 'light');
    this.applyTheme();
  }

  private applyTheme() {
    if (this.isDarkMode) {
      document.body.classList.add('dark-theme');
    } else {
      document.body.classList.remove('dark-theme');
    }
  }

  get user() {
    return this.currentUserService.user();
  }

  get userInitials(): string {
    if (!this.user) return '?';
    const first = this.user.firstName?.charAt(0) || '';
    const last = this.user.lastName?.charAt(0) || '';
    return (first + last).toUpperCase() || this.user.username?.charAt(0)?.toUpperCase() || '?';
  }
}
