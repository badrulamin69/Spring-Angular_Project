export interface GeneratedReport {
  id?: number;
  uniqueCode?: string;
  templateId?: number;
  title: string;
  reportType: string;
  parameters?: string;
  fileUrl?: string;
  format?: string;
  generatedBy?: number;
  generatedAt?: string;
  createdAt?: string;
  updatedAt?: string;
}
