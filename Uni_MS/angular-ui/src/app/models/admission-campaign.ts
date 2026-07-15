export interface AdmissionCampaign {
  id?: number;
  uniqueCode?: string;
  name: string;
  type?: string;
  description?: string;
  budget?: number;
  spent?: number;
  startDate: string;
  endDate: string;
  status: string;
  targetAudience?: string;
  channels?: string;
  applicationsGenerated?: number;
  enrollmentsConverted?: number;
  notes?: string;
  sessionId?: number;
  createdAt?: string;
  updatedAt?: string;
}
