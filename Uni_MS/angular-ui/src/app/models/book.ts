export interface Book {
  id?: number;
  uniqueCode?: string;
  title: string;
  author: string;
  isbn?: string;
  publisher?: string;
  publicationYear?: number;
  edition?: string;
  totalCopies: number;
  availableCopies: number;
  shelfLocation?: string;
  categoryId?: number;
  createdAt?: string;
  updatedAt?: string;
}
