import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { StudentPromotion } from '../models/student-promotion';

@Injectable({ providedIn: 'root' })
export class StudentPromotionService {
  private apiUrl = `${environment.apiUrl}/student-promotions`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<StudentPromotion>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sort', params.sortBy)
      .set('direction', params.sortDir);
    return this.http.get<PagedResponse<StudentPromotion>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<StudentPromotion> {
    return this.http.get<StudentPromotion>(`${this.apiUrl}/${id}`);
  }

  create(data: StudentPromotion): Observable<StudentPromotion> {
    return this.http.post<StudentPromotion>(this.apiUrl, data);
  }

  update(id: number, data: StudentPromotion): Observable<StudentPromotion> {
    return this.http.put<StudentPromotion>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`);
  }
}
