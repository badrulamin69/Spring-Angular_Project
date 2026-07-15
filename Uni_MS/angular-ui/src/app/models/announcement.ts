export interface Announcement {
  id?: number;
  uniqueCode?: string;
  title: string;
  content: string;
  type?: string;
  postedBy?: number;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
