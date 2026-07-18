import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FeeStructure } from '../models/fee-structure';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class FeeStructureService {
  private apiUrl = `${environment.apiUrl}/fee-structures`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<FeeStructure>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<FeeStructure>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<FeeStructure> {
    return this.http.get<FeeStructure>(`${this.apiUrl}/${id}`);
  }

  save(data: FeeStructure): Observable<FeeStructure> {
    return this.http.post<FeeStructure>(this.apiUrl, data);
  }

  update(id: number, data: FeeStructure): Observable<FeeStructure> {
    return this.http.put<FeeStructure>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  findBySemesterAndProgram(semesterId: number, programId: number): Observable<FeeStructure[]> {
    return this.http.get<FeeStructure[]>(`${this.apiUrl}/semester/${semesterId}/program/${programId}`);
  }
}
