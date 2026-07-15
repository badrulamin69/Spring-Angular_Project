export interface Payroll {
  id?: number;
  uniqueCode?: string;
  employeeId: number;
  payPeriodStart: string;
  payPeriodEnd: string;
  basicSalary: number;
  allowances?: number;
  deductions?: number;
  netSalary: number;
  paymentDate?: string;
  status?: string;
  createdAt?: string;
  updatedAt?: string;
}
