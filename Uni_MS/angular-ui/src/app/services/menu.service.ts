import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface MenuItem {
  id: number;
  title: string;
  icon: string;
  route: string;
  orderNo: number;
  permissionCode: string;
  module: string;
  visible: boolean;
  children?: MenuItem[];
  _expanded?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class MenuService {
  private apiUrl = `${environment.apiUrl}/menus`;
  private menusSubject = new BehaviorSubject<MenuItem[]>([]);
  menus$ = this.menusSubject.asObservable();

  constructor(private http: HttpClient) {}

  getMyMenus(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/my`).pipe(
      tap(response => {
        const menus = response?.data || response || [];
        this.menusSubject.next(menus);
      })
    );
  }

  getAllMenus(): Observable<any> {
    return this.http.get<any>(this.apiUrl);
  }

  createMenu(menu: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, menu);
  }

  updateMenu(id: number, menu: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, menu);
  }

  deleteMenu(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }

  getMenus(): MenuItem[] {
    return this.menusSubject.value;
  }

  clearMenus(): void {
    this.menusSubject.next([]);
  }
}
