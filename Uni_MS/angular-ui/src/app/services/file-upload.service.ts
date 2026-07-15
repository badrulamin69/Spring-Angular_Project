import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class FileUploadService {
  private apiUrl = `${environment.apiUrl}/upload`;

  constructor(private http: HttpClient) {}

  upload(file: File, module: string): Observable<{ url: string; filename: string }> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{ url: string; filename: string }>(`${this.apiUrl}/${module}`, formData);
  }

  uploadMultiple(files: File[], module: string): Observable<{ url: string; filename: string }[]> {
    const formData = new FormData();
    files.forEach(f => formData.append('files', f));
    return this.http.post<{ url: string; filename: string }[]>(`${this.apiUrl}/${module}/multiple`, formData);
  }

  delete(url: string): Observable<any> {
    return this.http.delete(this.apiUrl, { params: { url } });
  }
}
