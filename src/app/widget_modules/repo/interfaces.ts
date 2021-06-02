export interface IRepoResponse {
  result: IRepo[];
  lastUpdated: number;
}

export interface IRepo {
  id: string;
  collectorItemId: string;
  scmRevisionNumber: string;
  scmAuthor: string;
  scmCommitLog: string;
  scmCommitTimestamp: string;
  timestamp: string;
  number: string;
  mergedAt: string;
  mergeAuthor: string;
  userId: string;
}

