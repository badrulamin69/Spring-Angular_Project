export interface AdmissionTestQuestion {
  id?: number;
  questionText: string;
  optionA: string;
  optionB: string;
  optionC: string;
  optionD: string;
  optionE?: string;
  correctOption: string;
  marks: number;
  negativeMarks?: number;
  testId?: number;
  test?: any;
  subject?: string;
  difficulty?: string;
  explanation?: string;
  questionType?: string;
  isActive?: boolean;
}
