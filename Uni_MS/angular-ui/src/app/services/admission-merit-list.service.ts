import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { AdmissionMeritList } from '../models/admission-merit-list';
import { AdmissionMeritListEntry } from '../models/admission-merit-list-entry';

@Injectable({ providedIn: 'root' })
export class AdmissionMeritListService {
  private apiUrl = `${environment.apiUrl}/admission-merit-lists`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS): Observable<PagedResponse<AdmissionMeritList>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sort', params.sortBy)
      .set('direction', params.sortDir);
    return this.http.get<PagedResponse<AdmissionMeritList>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<AdmissionMeritList> {
    return this.http.get<AdmissionMeritList>(`${this.apiUrl}/${id}`);
  }

  create(data: AdmissionMeritList): Observable<AdmissionMeritList> {
    return this.http.post<AdmissionMeritList>(this.apiUrl, data);
  }

  update(id: number, data: AdmissionMeritList): Observable<AdmissionMeritList> {
    return this.http.put<AdmissionMeritList>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  publish(id: number): Observable<AdmissionMeritList> {
    return this.http.put<AdmissionMeritList>(`${this.apiUrl}/${id}/publish`, {});
  }

  getEntries(meritListId: number): Observable<AdmissionMeritListEntry[]> {
    return this.http.get<AdmissionMeritListEntry[]>(`${this.apiUrl}/${meritListId}/entries`);
  }

  addEntry(meritListId: number, entry: AdmissionMeritListEntry): Observable<AdmissionMeritListEntry> {
    return this.http.post<AdmissionMeritListEntry>(`${this.apiUrl}/${meritListId}/entries`, entry);
  }

  updateEntry(entryId: number, entry: AdmissionMeritListEntry): Observable<AdmissionMeritListEntry> {
    return this.http.put<AdmissionMeritListEntry>(`${this.apiUrl}/entries/${entryId}`, entry);
  }

  deleteEntry(entryId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/entries/${entryId}`);
  }

  getStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`);
  }
}
