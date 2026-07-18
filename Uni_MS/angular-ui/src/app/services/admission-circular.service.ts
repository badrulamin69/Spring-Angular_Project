import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { AdmissionCircular } from '../models/admission-circular';

@Injectable({ providedIn: 'root' })
export class AdmissionCircularService {
  private apiUrl = `${environment.apiUrl}/admission-circulars`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<AdmissionCircular>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sort', params.sortBy)
      .set('direction', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<AdmissionCircular>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<AdmissionCircular> {
    return this.http.get<AdmissionCircular>(`${this.apiUrl}/${id}`);
  }

  create(data: AdmissionCircular): Observable<AdmissionCircular> {
    return this.http.post<AdmissionCircular>(this.apiUrl, data);
  }

  update(id: number, data: AdmissionCircular): Observable<AdmissionCircular> {
    return this.http.put<AdmissionCircular>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
