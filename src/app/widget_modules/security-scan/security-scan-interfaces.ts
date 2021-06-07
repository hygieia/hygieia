export interface ISecurityScanResponse {
  result: ISecurityScan[];
  lastUpdated: string;
  reportUrl: string;
}

export interface ISecurityScan {
  id: string;
  collectorItemId: string;
  timestamp: number;
  type: string;
  metrics: IMetric[];
  url?: string;
  name?: string;
}

export interface IMetric {
  name: string;
  value: string;
  formattedValue: string;
  status: string;
  instances?: any;
}
