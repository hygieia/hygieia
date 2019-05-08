export interface IBuildResponse {
  result: IBuild[];
  lastUpdated: number;
}

export interface IBuild {
  id: string;
  collectorItemId: string;
  timestamp: number;
  number: string;
  buildUrl: string;
  startTime: number;
  endTime: number;
  duration: number;
  buildStatus: string;
  codeRepos: ICodeRepo[];
  sourceChangeSet: any[];
}

export interface ICodeRepo {
  url: string;
  branch: string;
  type: string;
}
