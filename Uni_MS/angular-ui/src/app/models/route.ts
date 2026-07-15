export interface Route {
  id?: number;
  uniqueCode?: string;
  name: string;
  routeCode: string;
  startPoint: string;
  endPoint: string;
  distanceKm?: number;
  fare?: number;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
