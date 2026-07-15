export interface CourseAssignment {
  id?: number;
  uniqueCode?: string;
  courseId: number;
  subjectId: number;
  administrationId?: number;
  semester: number;
  createdAt?: string;
  updatedAt?: string;
}
