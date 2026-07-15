export interface Sport {
  id?: number;
  uniqueCode?: string;
  name: string;
  code: string;
  description?: string;
  coachName?: string;
  maxParticipants?: number;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
