import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdmissionMeritList } from '../models/admission-merit-list';
import { AdmissionMeritListEntry } from '../models/admission-merit-list-entry';
import { environment } from '../../environments/environment';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';

@Injectable({ providedIn: 'root' })
export class AdmissionMeritListService {
  private apiUrl = `${environment.apiUrl}/admission-merit-lists`;
  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, filters: any = {}): Observable<PagedResponse<AdmissionMeritList>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (filters.search) httpParams = httpParams.set('search', filters.search);
    if (filters.status) httpParams = httpParams.set('status', filters.status);
    if (filters.sessionId) httpParams = httpParams.set('sessionId', filters.sessionId.toString());
    if (filters.facultyId) httpParams = httpParams.set('facultyId', filters.facultyId.toString());
    if (filters.departmentId) httpParams = httpParams.set('departmentId', filters.departmentId.toString());
    if (filters.programId) httpParams = httpParams.set('programId', filters.programId.toString());
    if (filters.testId) httpParams = httpParams.set('testId', filters.testId.toString());
    return this.http.get<PagedResponse<AdmissionMeritList>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<AdmissionMeritList> {
    return this.http.get<AdmissionMeritList>(`${this.apiUrl}/${id}`);
  }

  save(data: AdmissionMeritList): Observable<AdmissionMeritList> {
    return this.http.post<AdmissionMeritList>(this.apiUrl, data);
  }

  update(id: number, data: AdmissionMeritList): Observable<AdmissionMeritList> {
    return this.http.put<AdmissionMeritList>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  generate(testId: number, listName?: string, totalSeats?: number, academicYear?: string,
           facultyId?: number, departmentId?: number, programId?: number, shift?: string): Observable<AdmissionMeritList> {
    let params = new HttpParams().set('testId', testId.toString());
    if (listName) params = params.set('listName', listName);
    if (totalSeats) params = params.set('totalSeats', totalSeats.toString());
    if (academicYear) params = params.set('academicYear', academicYear);
    if (facultyId) params = params.set('facultyId', facultyId.toString());
    if (departmentId) params = params.set('departmentId', departmentId.toString());
    if (programId) params = params.set('programId', programId.toString());
    if (shift) params = params.set('shift', shift);
    return this.http.post<AdmissionMeritList>(`${this.apiUrl}/generate`, {}, { params });
  }

  publish(id: number): Observable<AdmissionMeritList> {
    return this.http.put<AdmissionMeritList>(`${this.apiUrl}/${id}/publish`, {});
  }

  unpublish(id: number): Observable<AdmissionMeritList> {
    return this.http.put<AdmissionMeritList>(`${this.apiUrl}/${id}/unpublish`, {});
  }

  archive(id: number): Observable<AdmissionMeritList> {
    return this.http.put<AdmissionMeritList>(`${this.apiUrl}/${id}/archive`, {});
  }

  getStats(): Observable<any> {
    return this.http.get(`${this.apiUrl}/stats`);
  }

  getEntries(meritListId: number, params: PageParams = DEFAULT_PAGE_PARAMS, filters: any = {}): Observable<PagedResponse<AdmissionMeritListEntry>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (filters.search) httpParams = httpParams.set('search', filters.search);
    if (filters.status) httpParams = httpParams.set('status', filters.status);
    if (filters.quotaType) httpParams = httpParams.set('quotaType', filters.quotaType);
    return this.http.get<PagedResponse<AdmissionMeritListEntry>>(`${this.apiUrl}/${meritListId}/entries`, { params: httpParams });
  }

  getAllEntries(meritListId: number): Observable<AdmissionMeritListEntry[]> {
    return this.http.get<AdmissionMeritListEntry[]>(`${this.apiUrl}/${meritListId}/entries/all`);
  }

  updateEntryStatus(entryId: number, status: string): Observable<AdmissionMeritListEntry> {
    return this.http.put<AdmissionMeritListEntry>(`${this.apiUrl}/entries/${entryId}/status`, null, { params: { status } });
  }

  deleteEntry(entryId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/entries/${entryId}`);
  }
}
