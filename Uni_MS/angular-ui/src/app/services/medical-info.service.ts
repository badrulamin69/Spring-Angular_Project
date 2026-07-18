import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { MedicalInfo } from '../models/medical-info';

@Injectable({ providedIn: 'root' })
export class MedicalInfoService {
  private apiUrl = `${environment.apiUrl}/medical-info`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<MedicalInfo>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sort', params.sortBy)
      .set('direction', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<MedicalInfo>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<MedicalInfo> {
    return this.http.get<MedicalInfo>(`${this.apiUrl}/${id}`);
  }

  create(data: MedicalInfo): Observable<MedicalInfo> {
    return this.http.post<MedicalInfo>(this.apiUrl, data);
  }

  update(id: number, data: MedicalInfo): Observable<MedicalInfo> {
    return this.http.put<MedicalInfo>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`);
  }
}
