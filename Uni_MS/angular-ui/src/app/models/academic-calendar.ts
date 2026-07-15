export interface AcademicCalendar {
  id?: number;
  uniqueCode?: string;
  title: string;
  description?: string;
  eventType: string;
  startDate: string;
  endDate?: string;
  semesterId: number;
  isHoliday?: boolean;
  isPublished?: boolean;
  color?: string;
  createdAt?: string;
  updatedAt?: string;
}
