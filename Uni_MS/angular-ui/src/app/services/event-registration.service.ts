import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EventRegistration } from '../models/event-registration';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class EventRegistrationService {
  private apiUrl = `${environment.apiUrl}/event-registrations`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<EventRegistration>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<EventRegistration>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<EventRegistration> {
    return this.http.get<EventRegistration>(`${this.apiUrl}/${id}`);
  }

  save(eventRegistration: EventRegistration): Observable<EventRegistration> {
    return this.http.post<EventRegistration>(this.apiUrl, eventRegistration);
  }

  update(id: number, eventRegistration: EventRegistration): Observable<EventRegistration> {
    return this.http.put<EventRegistration>(`${this.apiUrl}/${id}`, eventRegistration);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
