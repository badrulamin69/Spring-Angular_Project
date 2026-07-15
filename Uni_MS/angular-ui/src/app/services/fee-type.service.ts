import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FeeType } from '../models/fee-type';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class FeeTypeService {
  private apiUrl = `${environment.apiUrl}/fee-types`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<FeeType>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    return this.http.get<PagedResponse<FeeType>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<FeeType> {
    return this.http.get<FeeType>(`${this.apiUrl}/${id}`);
  }

  save(feeType: FeeType): Observable<FeeType> {
    return this.http.post<FeeType>(this.apiUrl, feeType);
  }

  update(id: number, feeType: FeeType): Observable<FeeType> {
    return this.http.put<FeeType>(`${this.apiUrl}/${id}`, feeType);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
