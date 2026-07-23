import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface EntityAttachment {
  id?: number;
  entityType: string;
  entityId: number;
  originalFilename?: string;
  storedFilename?: string;
  path?: string;
  contentType?: string;
  size?: number;
  uploadedBy?: any;
  category?: string;
  verified?: boolean;
  status?: string;
  formattedSize?: string;
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
export class AttachmentService {
  private apiUrl = `${environment.apiUrl}/attachments`;

  constructor(private http: HttpClient) {}

  getAttachments(entityType: string, entityId: number, page = 0, size = 20): Observable<PagedResponse<EntityAttachment>> {
    const params = new HttpParams()
      .set('entityType', entityType)
      .set('entityId', entityId.toString())
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PagedResponse<EntityAttachment>>(this.apiUrl, { params });
  }

  countAttachments(entityType: string, entityId: number): Observable<{ count: number }> {
    const params = new HttpParams()
      .set('entityType', entityType)
      .set('entityId', entityId.toString());
    return this.http.get<{ count: number }>(`${this.apiUrl}/count`, { params });
  }

  uploadAttachment(entityType: string, entityId: number, file: File, category?: string): Observable<EntityAttachment> {
    const formData = new FormData();
    formData.append('file', file);
    let params = new HttpParams()
      .set('entityType', entityType)
      .set('entityId', entityId.toString());
    if (category) params = params.set('category', category);
    return this.http.post<EntityAttachment>(`${this.apiUrl}/upload`, formData, { params });
  }

  deleteAttachment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
