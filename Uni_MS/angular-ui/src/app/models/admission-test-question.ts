export interface AdmissionTestQuestion {
  id?: number;
  questionText: string;
  optionA: string;
  optionB: string;
  optionC: string;
  optionD: string;
  correctOption: string;
  marks: number;
  testId?: number;
  test?: any;
}
