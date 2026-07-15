import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { StudentDocument } from '../models/student-document';

@Injectable({ providedIn: 'root' })
export class StudentDocumentService {
  private apiUrl = `${environment.apiUrl}/student-documents`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<StudentDocument>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sort', params.sortBy)
      .set('direction', params.sortDir);
    return this.http.get<PagedResponse<StudentDocument>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<StudentDocument> {
    return this.http.get<StudentDocument>(`${this.apiUrl}/${id}`);
  }

  create(data: StudentDocument): Observable<StudentDocument> {
    return this.http.post<StudentDocument>(this.apiUrl, data);
  }

  update(id: number, data: StudentDocument): Observable<StudentDocument> {
    return this.http.put<StudentDocument>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`);
  }
}
