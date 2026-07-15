import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Teacher } from '../models/teacher';
import { PagedResponse, PageParams, DEFAULT_PAGE_PARAMS } from '../models/paged-response';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class TeacherService {
  private apiUrl = `${environment.apiUrl}/teachers`;

  constructor(private http: HttpClient) {}

  findAll(params: PageParams = DEFAULT_PAGE_PARAMS, search: string = '', departmentId?: number, facultyId?: number, designation?: string, status?: string): Observable<PagedResponse<Teacher>> {
    let httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString())
      .set('sortBy', params.sortBy)
      .set('sortDir', params.sortDir);
    if (search) httpParams = httpParams.set('search', search);
    if (departmentId) httpParams = httpParams.set('departmentId', departmentId.toString());
    if (facultyId) httpParams = httpParams.set('facultyId', facultyId.toString());
    if (designation) httpParams = httpParams.set('designation', designation);
    if (status) httpParams = httpParams.set('status', status);
    return this.http.get<PagedResponse<Teacher>>(this.apiUrl, { params: httpParams });
  }

  findById(id: number): Observable<Teacher> {
    return this.http.get<Teacher>(`${this.apiUrl}/${id}`);
  }

  save(teacher: Teacher): Observable<Teacher> {
    return this.http.post<Teacher>(this.apiUrl, teacher);
  }

  update(id: number, teacher: Teacher): Observable<Teacher> {
    return this.http.put<Teacher>(`${this.apiUrl}/${id}`, teacher);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getDashboard(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/dashboard`);
  }

  getDocuments(teacherId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${teacherId}/documents`);
  }

  addDocument(teacherId: number, doc: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${teacherId}/documents`, doc);
  }

  getCourseAssignments(teacherId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${teacherId}/course-assignments`);
  }

  getPublications(teacherId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${teacherId}/publications`);
  }

  getLeaves(teacherId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${teacherId}/leaves`);
  }

  getAttendance(teacherId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${teacherId}/attendance`);
  }
}
