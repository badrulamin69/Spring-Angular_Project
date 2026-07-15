export interface ReportTemplate {
  id?: number;
  uniqueCode?: string;
  name: string;
  code: string;
  description?: string;
  reportType: string;
  templateConfig?: string;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
