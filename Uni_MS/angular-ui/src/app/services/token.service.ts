import { Injectable } from '@angular/core';

const TOKEN_KEY = 'auth-token';
const REFRESH_KEY = 'auth-refresh';
const USER_KEY = 'auth-user';
const REMEMBER_KEY = 'auth-remember';

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  private get storage(): Storage {
    return this.isRememberMe() ? window.localStorage : window.sessionStorage;
  }

  signOut(): void {
    window.localStorage.removeItem(TOKEN_KEY);
    window.localStorage.removeItem(REFRESH_KEY);
    window.localStorage.removeItem(USER_KEY);
    window.localStorage.removeItem(REMEMBER_KEY);
    window.sessionStorage.removeItem(TOKEN_KEY);
    window.sessionStorage.removeItem(REFRESH_KEY);
    window.sessionStorage.removeItem(USER_KEY);
  }

  saveToken(token: string): void {
    this.storage.setItem(TOKEN_KEY, token);
  }

  getToken(): string | null {
    return this.storage.getItem(TOKEN_KEY);
  }

  saveRefreshToken(token: string): void {
    this.storage.setItem(REFRESH_KEY, token);
  }

  getRefreshToken(): string | null {
    return this.storage.getItem(REFRESH_KEY);
  }

  saveUser(user: any): void {
    this.storage.setItem(USER_KEY, JSON.stringify(user));
  }

  getUser(): any {
    const user = this.storage.getItem(USER_KEY);
    if (!user) return null;
    try {
      return JSON.parse(user);
    } catch {
      this.storage.removeItem(USER_KEY);
      return null;
    }
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  setRememberMe(remember: boolean): void {
    window.localStorage.setItem(REMEMBER_KEY, JSON.stringify(remember));
  }

  isRememberMe(): boolean {
    const val = window.localStorage.getItem(REMEMBER_KEY);
    return val ? JSON.parse(val) : false;
  }
}
