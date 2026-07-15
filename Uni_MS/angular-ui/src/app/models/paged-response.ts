export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface PageParams {
  page: number;
  size: number;
  sortBy: string;
  sortDir: 'asc' | 'desc';
}

export const DEFAULT_PAGE_PARAMS: PageParams = {
  page: 0,
  size: 20,
  sortBy: 'id',
  sortDir: 'asc'
};
