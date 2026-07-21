import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { RegistrationConfig } from '../models/registration';

@Injectable({ providedIn: 'root' })
export class RegistrationConfigService {
  private apiUrl = `${environment.apiUrl}/registration-configs`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<RegistrationConfig[]> {
    return this.http.get<RegistrationConfig[]>(this.apiUrl);
  }

  findActive(): Observable<RegistrationConfig[]> {
    return this.http.get<RegistrationConfig[]>(`${this.apiUrl}/active`);
  }

  findById(id: number): Observable<RegistrationConfig> {
    return this.http.get<RegistrationConfig>(`${this.apiUrl}/${id}`);
  }

  findBySemester(semesterId: number): Observable<RegistrationConfig> {
    return this.http.get<RegistrationConfig>(`${this.apiUrl}/semester/${semesterId}`);
  }

  create(data: RegistrationConfig): Observable<RegistrationConfig> {
    return this.http.post<RegistrationConfig>(this.apiUrl, data);
  }

  update(id: number, data: RegistrationConfig): Observable<RegistrationConfig> {
    return this.http.put<RegistrationConfig>(`${this.apiUrl}/${id}`, data);
  }

  closeRegistration(id: number): Observable<RegistrationConfig> {
    return this.http.post<RegistrationConfig>(`${this.apiUrl}/${id}/close`, {});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
