export interface TimeSlot {
  id?: number;
  name: string;
  code: string;
  startTime: string;
  endTime: string;
  slotType: string;
  durationMinutes: number;
  sortOrder: number;
  isActive: boolean;
  remarks?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Building {
  id?: number;
  name: string;
  code: string;
  description?: string;
  address?: string;
  totalFloors: number;
  totalRooms: number;
  contactPerson?: string;
  contactPhone?: string;
  isActive: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface Classroom {
  id?: number;
  buildingId: number;
  buildingName?: string;
  buildingCode?: string;
  roomNumber: string;
  floor: number;
  capacity: number;
  roomType: string;
  isLab: boolean;
  isSmartClassroom: boolean;
  hasProjector: boolean;
  hasWhiteboard: boolean;
  hasWifi: boolean;
  equipment?: string;
  isAvailable: boolean;
  isActive: boolean;
  remarks?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface ClassRoutine {
  id?: number;
  subjectId: number;
  subjectName?: string;
  subjectCode?: string;
  administrationId: number;
  teacherName?: string;
  sectionId: number;
  sectionName?: string;
  semesterId: number;
  semesterName?: string;
  batchId?: number;
  batchName?: string;
  timeSlotId?: number;
  timeSlotName?: string;
  classroomId?: number;
  classroomNumber?: string;
  buildingName?: string;
  departmentName?: string;
  programName?: string;
  dayOfWeek: string;
  startTime: string;
  endTime: string;
  room?: string;
  building?: string;
  classType: string;
  shift?: string;
  isActive: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface ClassRoutineRequest {
  subjectId: number;
  administrationId: number;
  sectionId: number;
  semesterId: number;
  timeSlotId?: number;
  classroomId?: number;
  dayOfWeek: string;
  startTime: string;
  endTime: string;
  room?: string;
  building?: string;
  classType: string;
  shift?: string;
  isActive: boolean;
}

export interface ConflictCheckResponse {
  hasConflict: boolean;
  conflictType: string;
  conflictMessage: string;
  conflictingRoutineId: number;
  conflictingDetails: string;
}

export interface AcademicCalendarEvent {
  id?: number;
  title: string;
  description?: string;
  eventType: string;
  startDate: string;
  endDate?: string;
  startTime?: string;
  endTime?: string;
  semesterId: number;
  semesterName?: string;
  academicSessionId?: number;
  academicSessionName?: string;
  isHoliday: boolean;
  isPublished: boolean;
  isAllDay: boolean;
  color?: string;
  location?: string;
  recurrence?: string;
  notifyStudents: boolean;
  notifyTeachers: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface AcademicCalendar {
  id?: number;
  title: string;
  description?: string;
  eventType: string;
  startDate: string;
  endDate?: string;
  semesterId: number;
  semesterName?: string;
  isHoliday: boolean;
  isPublished: boolean;
  color?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
}
