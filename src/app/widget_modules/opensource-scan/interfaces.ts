export interface IOpensourceScanResponse {
  result: IOpensourceScan[];
  lastUpdated: number;
}

export interface IOpensourceScan {
  id: string;
  collectorItemId: string;
  timestamp: number;
  threats: {
    License: IThreat[];
    Security: IThreat[];
  };
  reportUrl: string;
  scanState: string;
}

export interface IThreat {
  level: string;
  components: string[];
  count: number;
  dispositionCounts: {
    Open: number;
    Closed: number;
  };
  maxAge: number;
}
