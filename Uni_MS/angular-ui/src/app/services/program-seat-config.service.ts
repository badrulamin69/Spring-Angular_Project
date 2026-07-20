import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ProgramSeatConfig } from '../models/seat-allocation';

@Injectable({ providedIn: 'root' })
export class ProgramSeatConfigService {
  private apiUrl = `${environment.apiUrl}/program-seat-configs`;

  constructor(private http: HttpClient) {}

  findByConfigId(configId: number): Observable<ProgramSeatConfig[]> {
    return this.http.get<ProgramSeatConfig[]>(`${this.apiUrl}/config/${configId}`);
  }

  findById(id: number): Observable<ProgramSeatConfig> {
    return this.http.get<ProgramSeatConfig>(`${this.apiUrl}/${id}`);
  }

  save(data: ProgramSeatConfig): Observable<ProgramSeatConfig> {
    return this.http.post<ProgramSeatConfig>(this.apiUrl, data);
  }

  update(id: number, data: ProgramSeatConfig): Observable<ProgramSeatConfig> {
    return this.http.put<ProgramSeatConfig>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getAvailable(configId: number): Observable<ProgramSeatConfig[]> {
    return this.http.get<ProgramSeatConfig[]>(`${this.apiUrl}/config/${configId}/available`);
  }

  getSummary(configId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/config/${configId}/summary`);
  }
}
