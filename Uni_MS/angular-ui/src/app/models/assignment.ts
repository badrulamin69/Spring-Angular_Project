export interface Assignment {
  id?: number;
  uniqueCode?: string;
  title: string;
  description?: string;
  dueDate: string;
  maxMarks: number;
  courseId: number;
  subjectId: number;
  sectionId?: number;
  administrationId: number;
  createdAt?: string;
  updatedAt?: string;
}
