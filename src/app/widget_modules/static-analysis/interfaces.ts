export interface IStaticAnalysisResponse {
  result: IStaticAnalysis[];
  lastUpdated: number;
}

export interface IStaticAnalysis {
  id: string;
  collectorItemId: string;
  timestamp: number;
  name: string;
  url: string;
  version: string;
  metrics: IMetric[];
}

export interface IMetric {
  name: string;
  value: string;
  formattedValue: string;
}
