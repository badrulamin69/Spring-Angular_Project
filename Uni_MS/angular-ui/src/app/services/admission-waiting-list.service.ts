import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { AdmissionWaitingList } from '../models/admission-waiting-list';
import { AdmissionWaitingListEntry } from '../models/admission-waiting-list-entry';

@Injectable({ providedIn: 'root' })
export class AdmissionWaitingListService {
  private apiUrl = `${environment.apiUrl}/admission-waiting-lists`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = ''): Observable<PagedResponse<AdmissionWaitingList>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sort', params.sortBy)
      .set('direction', params.sortDir);
    if (search) {
      httpParams = httpParams.set('search', search);
    }
    return this.http.get<PagedResponse<AdmissionWaitingList>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<AdmissionWaitingList> {
    return this.http.get<AdmissionWaitingList>(`${this.apiUrl}/${id}`);
  }

  create(data: AdmissionWaitingList): Observable<AdmissionWaitingList> {
    return this.http.post<AdmissionWaitingList>(this.apiUrl, data);
  }

  update(id: number, data: AdmissionWaitingList): Observable<AdmissionWaitingList> {
    return this.http.put<AdmissionWaitingList>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getEntries(waitingListId: number): Observable<AdmissionWaitingListEntry[]> {
    return this.http.get<AdmissionWaitingListEntry[]>(`${this.apiUrl}/${waitingListId}/entries`);
  }

  addEntry(waitingListId: number, entry: AdmissionWaitingListEntry): Observable<AdmissionWaitingListEntry> {
    return this.http.post<AdmissionWaitingListEntry>(`${this.apiUrl}/${waitingListId}/entries`, entry);
  }

  updateEntry(entryId: number, entry: AdmissionWaitingListEntry): Observable<AdmissionWaitingListEntry> {
    return this.http.put<AdmissionWaitingListEntry>(`${this.apiUrl}/entries/${entryId}`, entry);
  }

  deleteEntry(entryId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/entries/${entryId}`);
  }

  getStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`);
  }
}
