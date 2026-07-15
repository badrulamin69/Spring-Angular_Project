import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ClassRoutine } from '../models/class-routine';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class ClassRoutineService {
  private apiUrl = `${environment.apiUrl}/class-routines`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<ClassRoutine>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<ClassRoutine>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<ClassRoutine> {
    return this.http.get<ClassRoutine>(`${this.apiUrl}/${id}`);
  }

  save(classRoutine: ClassRoutine): Observable<ClassRoutine> {
    return this.http.post<ClassRoutine>(this.apiUrl, classRoutine);
  }

  update(id: number, classRoutine: ClassRoutine): Observable<ClassRoutine> {
    return this.http.put<ClassRoutine>(`${this.apiUrl}/${id}`, classRoutine);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
