import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SemesterRoutine } from '../models/semester-routine';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class SemesterRoutineService {
  private apiUrl = `${environment.apiUrl}/semester-routines`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<SemesterRoutine>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<SemesterRoutine>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<SemesterRoutine> {
    return this.http.get<SemesterRoutine>(`${this.apiUrl}/${id}`);
  }

  save(semesterRoutine: SemesterRoutine): Observable<SemesterRoutine> {
    return this.http.post<SemesterRoutine>(this.apiUrl, semesterRoutine);
  }

  update(id: number, semesterRoutine: SemesterRoutine): Observable<SemesterRoutine> {
    return this.http.put<SemesterRoutine>(`${this.apiUrl}/${id}`, semesterRoutine);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
