export interface OnlineClass {
  id?: number;
  uniqueCode?: string;
  title: string;
  description?: string;
  meetingUrl: string;
  classDate: string;
  startTime: string;
  endTime: string;
  courseId: number;
  subjectId: number;
  administrationId: number;
  recordingUrl?: string;
  createdAt?: string;
  updatedAt?: string;
}
