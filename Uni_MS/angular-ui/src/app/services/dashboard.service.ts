import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CurrentUserService } from './current-user.service';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private http = inject(HttpClient);
  private currentUser = inject(CurrentUserService);
  private apiUrl = `${environment.apiUrl}/dashboards`;

  getDashboard(): Observable<any> {
    const role = this.currentUser.roleCode();
    const endpoints: Record<string, string> = {
      'ROLE_SUPER_ADMIN': 'super-admin',
      'ROLE_ADMIN': 'super-admin',
      'ROLE_UNIVERSITY_ADMIN': 'university-admin',
      'ROLE_DEPT_HEAD': 'department-head',
      'ROLE_FACULTY': 'faculty',
      'ROLE_ADVISOR': 'faculty',
      'ROLE_ADMISSION_OFFICER': 'admission-officer',
      'ROLE_ACCOUNTS_OFFICER': 'accounts-officer',
      'ROLE_LIBRARIAN': 'librarian',
      'ROLE_HALL_PROVOST': 'university-admin',
      'ROLE_TRANSPORT_MANAGER': 'university-admin',
      'ROLE_GENERAL_STAFF': 'university-admin',
      'ROLE_APPLICANT': 'university-admin',
      'ROLE_STUDENT': 'student',
      'ROLE_REGISTRAR': 'registrar',
      'ROLE_HR_MANAGER': 'hr-manager',
    };
    const endpoint = endpoints[role || ''] || 'university-admin';
    return this.http.get<any>(`${this.apiUrl}/${endpoint}`);
  }
}
