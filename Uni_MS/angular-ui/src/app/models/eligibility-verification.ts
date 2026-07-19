export interface EligibilityVerification {
  id?: number;
  registrationId?: number;
  registration?: any;
  testId?: number;
  test?: any;
  status?: string;
  verifiedBy?: string;
  verifiedAt?: string;
  remarks?: string;
  sscGpaVerified?: boolean;
  hscGpaVerified?: boolean;
  documentsVerified?: boolean;
}
