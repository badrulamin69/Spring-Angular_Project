import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CourseMaterial } from '../models/course-material';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CourseMaterialService {
  private apiUrl = `${environment.apiUrl}/course-materials`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<CourseMaterial>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<CourseMaterial>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<CourseMaterial> {
    return this.http.get<CourseMaterial>(`${this.apiUrl}/${id}`);
  }

  save(courseMaterial: CourseMaterial): Observable<CourseMaterial> {
    return this.http.post<CourseMaterial>(this.apiUrl, courseMaterial);
  }

  update(id: number, courseMaterial: CourseMaterial): Observable<CourseMaterial> {
    return this.http.put<CourseMaterial>(`${this.apiUrl}/${id}`, courseMaterial);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
