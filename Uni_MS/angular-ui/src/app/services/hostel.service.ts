import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Hostel } from '../models/hostel';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class HostelService {
  private apiUrl = `${environment.apiUrl}/hostels`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<Hostel>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<Hostel>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<Hostel> {
    return this.http.get<Hostel>(`${this.apiUrl}/${id}`);
  }

  save(hostel: Hostel): Observable<Hostel> {
    return this.http.post<Hostel>(this.apiUrl, hostel);
  }

  update(id: number, hostel: Hostel): Observable<Hostel> {
    return this.http.put<Hostel>(`${this.apiUrl}/${id}`, hostel);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
