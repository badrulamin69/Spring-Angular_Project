export interface ClassRoutine {
  id?: number;
  uniqueCode?: string;
  subjectId: number;
  administrationId: number;
  sectionId: number;
  semesterId: number;
  dayOfWeek: string;
  startTime: string;
  endTime: string;
  room?: string;
  building?: string;
  classType?: string;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
