import { Injectable, signal, computed, inject } from '@angular/core';
import { TokenService } from './token.service';

export interface CurrentUser {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  avatar: string;
  roleCode: string;
  roleName: string;
  permissions: string[];
  menus: any[];
}

@Injectable({
  providedIn: 'root'
})
export class CurrentUserService {
  private tokenService = inject(TokenService);
  private userState = signal<CurrentUser | null>(this.tokenService.getUser());

  readonly user = this.userState.asReadonly();
  readonly isLoggedIn = computed(() => !!this.userState());
  readonly roleCode = computed(() => this.userState()?.roleCode || null);
  readonly roleName = computed(() => this.userState()?.roleName || null);
  readonly permissions = computed<string[]>(() => this.userState()?.permissions || []);
  readonly menus = computed<any[]>(() => this.userState()?.menus || []);
  readonly fullName = computed(() => {
    const u = this.userState();
    return u ? `${u.firstName || ''} ${u.lastName || ''}`.trim() || u.username : '';
  });

  setUser(user: any): void {
    this.tokenService.saveUser(user);
    this.userState.set(user);
  }

  clearUser(): void {
    this.tokenService.signOut();
    this.userState.set(null);
  }

  hasPermission(permissionCode: string): boolean {
    return this.permissions().includes(permissionCode);
  }

  hasAnyPermission(...codes: string[]): boolean {
    return codes.some(c => this.permissions().includes(c));
  }
}
