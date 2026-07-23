import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface EntityComment {
  id?: number;
  entityType: string;
  entityId: number;
  user?: any;
  content: string;
  parentId?: number;
  edited?: boolean;
  status?: string;
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
export class CommentService {
  private apiUrl = `${environment.apiUrl}/comments`;

  constructor(private http: HttpClient) {}

  getComments(entityType: string, entityId: number, page = 0, size = 20): Observable<PagedResponse<EntityComment>> {
    const params = new HttpParams()
      .set('entityType', entityType)
      .set('entityId', entityId.toString())
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PagedResponse<EntityComment>>(this.apiUrl, { params });
  }

  countComments(entityType: string, entityId: number): Observable<{ count: number }> {
    const params = new HttpParams()
      .set('entityType', entityType)
      .set('entityId', entityId.toString());
    return this.http.get<{ count: number }>(`${this.apiUrl}/count`, { params });
  }

  addComment(comment: EntityComment): Observable<EntityComment> {
    return this.http.post<EntityComment>(this.apiUrl, comment);
  }

  updateComment(id: number, content: string): Observable<EntityComment> {
    return this.http.put<EntityComment>(`${this.apiUrl}/${id}`, { content });
  }

  deleteComment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
