export interface Event {
  id?: number;
  uniqueCode?: string;
  title: string;
  description?: string;
  eventType: string;
  startDate: string;
  endDate: string;
  venue?: string;
  clubId?: number;
  maxParticipants?: number;
  registrationFee?: number;
  status?: string;
  createdAt?: string;
  updatedAt?: string;
}
