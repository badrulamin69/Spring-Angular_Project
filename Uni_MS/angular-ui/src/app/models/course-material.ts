export interface CourseMaterial {
  id?: number;
  uniqueCode?: string;
  title: string;
  description?: string;
  materialType: string;
  fileUrl?: string;
  courseId: number;
  subjectId: number;
  administrationId: number;
  createdAt?: string;
  updatedAt?: string;
}
