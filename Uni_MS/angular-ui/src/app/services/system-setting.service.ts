import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class SystemSettingService {
  private baseUrl = `${environment.apiUrl}/system-settings`;

  constructor(private http: HttpClient) {}

  getDropdowns(): Observable<any> {
    return this.http.get(`${this.baseUrl}/dropdowns`);
  }

  findAll(): Observable<any> {
    return this.http.get(this.baseUrl);
  }

  findByModule(module: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/by-module/${module}`);
  }

  findByKey(key: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/by-key/${key}`);
  }

  findById(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

  findPublic(): Observable<any> {
    return this.http.get(`${this.baseUrl}/public`);
  }

  save(setting: any): Observable<any> {
    return this.http.post(this.baseUrl, setting);
  }

  update(id: number, setting: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, setting);
  }

  batchUpdate(settings: any[]): Observable<any> {
    return this.http.put(`${this.baseUrl}/batch`, settings);
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  deleteByKey(key: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/by-key/${key}`);
  }

  resetModule(module: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/reset/${module}`, {});
  }

  seedDefaults(): Observable<any> {
    return this.http.post(`${this.baseUrl}/seed`, {});
  }

  getSystemInfo(): Observable<any> {
    return this.http.get(`${this.baseUrl}/system-info`);
  }

  clearCache(): Observable<any> {
    return this.http.post(`${this.baseUrl}/clear-cache`, {});
  }
}
