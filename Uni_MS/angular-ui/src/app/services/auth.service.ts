import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { TokenService } from './token.service';
import { CurrentUserService } from './current-user.service';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;

  constructor(
    private http: HttpClient,
    private tokenService: TokenService,
    private currentUserService: CurrentUserService
  ) {}

  login(credentials: { username: string; password: string; rememberMe?: boolean }): Observable<any> {
    const rememberMe = credentials.rememberMe || false;
    this.tokenService.setRememberMe(rememberMe);
    return this.http.post<any>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        const token = response?.data?.token || response?.token;
        if (token) {
          this.tokenService.saveToken(token);
          const refreshToken = response?.data?.refreshToken || response?.refreshToken;
          if (refreshToken) {
            this.tokenService.saveRefreshToken(refreshToken);
          }
          const userData = response?.data || response;
          this.currentUserService.setUser(userData);
        }
      })
    );
  }

  register(data: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/register`, data);
  }

  logout(): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/logout`, {}).pipe(
      tap(() => {
        this.currentUserService.clearUser();
      })
    );
  }

  getMe(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/me`).pipe(
      tap(response => {
        if (response?.data) {
          this.currentUserService.setUser(response.data);
        }
      })
    );
  }

  refreshToken(): Observable<any> {
    const refreshToken = this.tokenService.getRefreshToken();
    return this.http.post<any>(`${this.apiUrl}/refresh`, { refreshToken }).pipe(
      tap(response => {
        const data = response?.data || response;
        const token = data?.token;
        if (token) {
          this.tokenService.saveToken(token);
          if (data.refreshToken) {
            this.tokenService.saveRefreshToken(data.refreshToken);
          }
        }
      })
    );
  }
}
