import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface TimelineEvent {
  id?: number;
  entityType: string;
  entityId: number;
  user?: any;
  eventType: string;
  description: string;
  oldValue?: string;
  newValue?: string;
  ipAddress?: string;
  severity?: string;
  createdAt?: string;
}

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

@Injectable({ providedIn: 'root' })
export class TimelineService {
  private apiUrl = `${environment.apiUrl}/api/v1/timeline`;

  constructor(private http: HttpClient) {}

  getTimeline(entityType: string, entityId: number, page = 0, size = 50): Observable<PagedResponse<TimelineEvent>> {
    const params = new HttpParams()
      .set('entityType', entityType)
      .set('entityId', entityId.toString())
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PagedResponse<TimelineEvent>>(this.apiUrl, { params });
  }

  getRecentTimeline(entityType: string, entityId: number): Observable<PagedResponse<TimelineEvent>> {
    const params = new HttpParams()
      .set('entityType', entityType)
      .set('entityId', entityId.toString());
    return this.http.get<PagedResponse<TimelineEvent>>(`${this.apiUrl}/recent`, { params });
  }

  countEvents(entityType: string, entityId: number): Observable<{ count: number }> {
    const params = new HttpParams()
      .set('entityType', entityType)
      .set('entityId', entityId.toString());
    return this.http.get<{ count: number }>(`${this.apiUrl}/count`, { params });
  }
}
