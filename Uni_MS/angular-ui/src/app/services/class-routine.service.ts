import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  TimeSlot,
  Building,
  Classroom,
  ClassRoutine,
  ClassRoutineRequest,
  ConflictCheckResponse,
  AcademicCalendarEvent
} from '../models/class-routine';

@Injectable({ providedIn: 'root' })
export class ClassRoutineService {
  private apiUrl = `${environment.apiUrl}`;

  constructor(private http: HttpClient) {}

  getTimeslots(): Observable<TimeSlot[]> {
    return this.http.get<TimeSlot[]>(`${this.apiUrl}/timeslots`);
  }

  getTimeslotById(id: number): Observable<TimeSlot> {
    return this.http.get<TimeSlot>(`${this.apiUrl}/timeslots/${id}`);
  }

  createTimeSlot(data: TimeSlot): Observable<TimeSlot> {
    return this.http.post<TimeSlot>(`${this.apiUrl}/timeslots`, data);
  }

  updateTimeSlot(id: number, data: TimeSlot): Observable<TimeSlot> {
    return this.http.put<TimeSlot>(`${this.apiUrl}/timeslots/${id}`, data);
  }

  deleteTimeSlot(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/timeslots/${id}`);
  }

  getBuildings(): Observable<Building[]> {
    return this.http.get<Building[]>(`${this.apiUrl}/buildings`);
  }

  getBuildingById(id: number): Observable<Building> {
    return this.http.get<Building>(`${this.apiUrl}/buildings/${id}`);
  }

  createBuilding(data: Building): Observable<Building> {
    return this.http.post<Building>(`${this.apiUrl}/buildings`, data);
  }

  updateBuilding(id: number, data: Building): Observable<Building> {
    return this.http.put<Building>(`${this.apiUrl}/buildings/${id}`, data);
  }

  deleteBuilding(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/buildings/${id}`);
  }

  getClassrooms(): Observable<Classroom[]> {
    return this.http.get<Classroom[]>(`${this.apiUrl}/classrooms`);
  }

  getClassroomById(id: number): Observable<Classroom> {
    return this.http.get<Classroom>(`${this.apiUrl}/classrooms/${id}`);
  }

  createClassroom(data: Classroom): Observable<Classroom> {
    return this.http.post<Classroom>(`${this.apiUrl}/classrooms`, data);
  }

  updateClassroom(id: number, data: Classroom): Observable<Classroom> {
    return this.http.put<Classroom>(`${this.apiUrl}/classrooms/${id}`, data);
  }

  deleteClassroom(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/classrooms/${id}`);
  }

  getClassRoutines(params?: { semesterId?: number; sectionId?: number; dayOfWeek?: string }): Observable<ClassRoutine[]> {
    let httpParams = new HttpParams();
    if (params?.semesterId) httpParams = httpParams.set('semesterId', params.semesterId.toString());
    if (params?.sectionId) httpParams = httpParams.set('sectionId', params.sectionId.toString());
    if (params?.dayOfWeek) httpParams = httpParams.set('dayOfWeek', params.dayOfWeek);
    return this.http.get<ClassRoutine[]>(`${this.apiUrl}/class-routines`, { params: httpParams });
  }

  getClassRoutineById(id: number): Observable<ClassRoutine> {
    return this.http.get<ClassRoutine>(`${this.apiUrl}/class-routines/${id}`);
  }

  createClassRoutine(data: ClassRoutineRequest): Observable<ClassRoutine> {
    return this.http.post<ClassRoutine>(`${this.apiUrl}/class-routines`, data);
  }

  updateClassRoutine(id: number, data: ClassRoutineRequest): Observable<ClassRoutine> {
    return this.http.put<ClassRoutine>(`${this.apiUrl}/class-routines/${id}`, data);
  }

  deleteClassRoutine(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/class-routines/${id}`);
  }

  getRoutinesBySemesterAndSection(semesterId: number, sectionId: number): Observable<ClassRoutine[]> {
    return this.http.get<ClassRoutine[]>(`${this.apiUrl}/class-routines/semester/${semesterId}/section/${sectionId}`);
  }

  getConflicts(data: ClassRoutineRequest): Observable<ConflictCheckResponse> {
    return this.http.post<ConflictCheckResponse>(`${this.apiUrl}/class-routines/check-conflicts`, data);
  }

  publishRoutine(semesterId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/class-routines/publish/${semesterId}`, {});
  }

  getCalendarEvents(): Observable<AcademicCalendarEvent[]> {
    return this.http.get<AcademicCalendarEvent[]>(`${this.apiUrl}/academic-calendar-events`);
  }

  getCalendarEventById(id: number): Observable<AcademicCalendarEvent> {
    return this.http.get<AcademicCalendarEvent>(`${this.apiUrl}/academic-calendar-events/${id}`);
  }

  createCalendarEvent(data: AcademicCalendarEvent): Observable<AcademicCalendarEvent> {
    return this.http.post<AcademicCalendarEvent>(`${this.apiUrl}/academic-calendar-events`, data);
  }

  updateCalendarEvent(id: number, data: AcademicCalendarEvent): Observable<AcademicCalendarEvent> {
    return this.http.put<AcademicCalendarEvent>(`${this.apiUrl}/academic-calendar-events/${id}`, data);
  }

  deleteCalendarEvent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/academic-calendar-events/${id}`);
  }

  getCalendarEventsBySemester(semesterId: number): Observable<AcademicCalendarEvent[]> {
    return this.http.get<AcademicCalendarEvent[]>(`${this.apiUrl}/academic-calendar-events/semester/${semesterId}`);
  }

  getUpcomingEvents(): Observable<AcademicCalendarEvent[]> {
    return this.http.get<AcademicCalendarEvent[]>(`${this.apiUrl}/academic-calendar-events/upcoming`);
  }

  getHolidays(semesterId: number): Observable<AcademicCalendarEvent[]> {
    return this.http.get<AcademicCalendarEvent[]>(`${this.apiUrl}/academic-calendar-events/holidays/${semesterId}`);
  }

  findAll(params?: any, search?: string): Observable<any> {
    let httpParams = new HttpParams();
    if (params) {
      if (params.page !== undefined) httpParams = httpParams.set('page', params.page.toString());
      if (params.size !== undefined) httpParams = httpParams.set('size', params.size.toString());
      if (params.sortBy) httpParams = httpParams.set('sortBy', params.sortBy);
      if (params.sortDir) httpParams = httpParams.set('sortDir', params.sortDir);
    }
    if (search) httpParams = httpParams.set('search', search);
    return this.http.get<any>(`${this.apiUrl}/class-routines`, { params: httpParams });
  }

  save(data: ClassRoutine): Observable<ClassRoutine> {
    return this.http.post<ClassRoutine>(`${this.apiUrl}/class-routines`, data);
  }

  update(id: number, data: ClassRoutine): Observable<ClassRoutine> {
    return this.http.put<ClassRoutine>(`${this.apiUrl}/class-routines/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/class-routines/${id}`);
  }
}
