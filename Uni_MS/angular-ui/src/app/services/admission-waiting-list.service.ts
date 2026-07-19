import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdmissionWaitingList } from '../models/admission-waiting-list';
import { AdmissionWaitingListEntry } from '../models/admission-waiting-list-entry';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class AdmissionWaitingListService {
  private apiUrl = `${environment.apiUrl}/admission-waiting-lists`;
  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, filters: any = {}): Observable<PagedResponse<AdmissionWaitingList>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (filters.search) httpParams = httpParams.set('search', filters.search);
    if (filters.status) httpParams = httpParams.set('status', filters.status);
    if (filters.sessionId) httpParams = httpParams.set('sessionId', filters.sessionId.toString());
    if (filters.facultyId) httpParams = httpParams.set('facultyId', filters.facultyId.toString());
    if (filters.programId) httpParams = httpParams.set('programId', filters.programId.toString());
    if (filters.testId) httpParams = httpParams.set('testId', filters.testId.toString());
    return this.http.get<PagedResponse<AdmissionWaitingList>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<AdmissionWaitingList> {
    return this.http.get<AdmissionWaitingList>(`${this.apiUrl}/${id}`);
  }

  save(data: AdmissionWaitingList): Observable<AdmissionWaitingList> {
    return this.http.post<AdmissionWaitingList>(this.apiUrl, data);
  }

  update(id: number, data: AdmissionWaitingList): Observable<AdmissionWaitingList> {
    return this.http.put<AdmissionWaitingList>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  generate(testId: number, listName?: string, totalSlots?: number, academicYear?: string,
           facultyId?: number, programId?: number): Observable<AdmissionWaitingList> {
    let params = new HttpParams().set('testId', testId.toString());
    if (listName) params = params.set('listName', listName);
    if (totalSlots) params = params.set('totalSlots', totalSlots.toString());
    if (academicYear) params = params.set('academicYear', academicYear);
    if (facultyId) params = params.set('facultyId', facultyId.toString());
    if (programId) params = params.set('programId', programId.toString());
    return this.http.post<AdmissionWaitingList>(`${this.apiUrl}/generate`, {}, { params });
  }

  publish(id: number): Observable<AdmissionWaitingList> {
    return this.http.put<AdmissionWaitingList>(`${this.apiUrl}/${id}/publish`, {});
  }

  unpublish(id: number): Observable<AdmissionWaitingList> {
    return this.http.put<AdmissionWaitingList>(`${this.apiUrl}/${id}/unpublish`, {});
  }

  getStats(): Observable<any> {
    return this.http.get(`${this.apiUrl}/stats`);
  }

  getEntries(waitingListId: number, params: PageParams = DEFAULT_PAGE_PARAMS, filters: any = {}): Observable<PagedResponse<AdmissionWaitingListEntry>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (filters.search) httpParams = httpParams.set('search', filters.search);
    if (filters.status) httpParams = httpParams.set('status', filters.status);
    return this.http.get<PagedResponse<AdmissionWaitingListEntry>>(`${this.apiUrl}/${waitingListId}/entries`, { params: httpParams });
  }

  getAllEntries(waitingListId: number): Observable<AdmissionWaitingListEntry[]> {
    return this.http.get<AdmissionWaitingListEntry[]>(`${this.apiUrl}/${waitingListId}/entries/all`);
  }

  updateEntryStatus(entryId: number, status: string): Observable<AdmissionWaitingListEntry> {
    return this.http.put<AdmissionWaitingListEntry>(`${this.apiUrl}/entries/${entryId}/status`, null, { params: { status } });
  }

  deleteEntry(entryId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/entries/${entryId}`);
  }
}
