export interface AdmissionTestAttempt {
  id?: number;
  registrationId: number;
  testId: number;
  answers?: string;
  totalQuestions?: number;
  correctAnswers?: number;
  score?: number;
  maxScore?: number;
  percentage?: number;
  timeTakenSeconds?: number;
  startedAt?: string;
  submittedAt?: string;
  status?: string;
}
