export interface IFeatureResponse {
  result: IFeature[];
  lastUpdated: number;
}

export interface IFeature {
  id: string;
  openEstimate: number;
  inProgressEstimate: number;
  completeEstimate: number;
}

