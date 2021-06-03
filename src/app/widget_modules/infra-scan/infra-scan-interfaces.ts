export interface InfraScanResponse {
  result: InfraScan[];
}

export interface InfraScan {
  id: string;
  collectorItemId: string;
  timestamp: number;
  type: string;
  businessService: string;
  businessApplication: string;
  instanceId: string;
  vulnerabilities: IVulnerability[];
}

export interface IVulnerability {
  contextualizedRiskScore: string;
  contextualizedRiskLabel: string;
  vulnerabilityTitle: string;
  vulnerabilityId: string;
  type: string;
  vulnerabilityStatus: string;
  result: string;
  consequence: string;
  solution: string;
}
