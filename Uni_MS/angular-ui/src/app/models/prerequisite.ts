export interface Prerequisite {
  id?: number;
  uniqueCode?: string;
  subjectId: number;
  prerequisiteSubjectId: number;
  minGrade?: string;
  isMandatory?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
