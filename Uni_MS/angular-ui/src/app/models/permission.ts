export interface Permission {
  id?: number;
  uniqueCode?: string;
  name: string;
  code: string;
  module: string;
  action: string;
  description?: string;
  createdAt?: string;
  updatedAt?: string;
}
