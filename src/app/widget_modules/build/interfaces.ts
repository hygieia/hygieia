export interface IBuildResponse {
  result: IBuild[];
  lastUpdated: number;
}

export interface IBuild {
  id: string;
  collectorItemId?: string;
  timestamp?: number;
  number?: string;
  buildUrl?: string;
  startTime?: number;
  endTime?: number;
  duration?: number;
  buildStatus?: string;
  codeRepos?: ICodeRepo[];
  sourceChangeSet?: any[];
  stages?: IStage[];
}

export interface ICodeRepo {
  url: string;
  branch: string;
  type: string;
}

export interface IStage {
  _id?: any;
  stageId?: string;
  name?: string;
  status?: string;
  startTimeMillis?: string;
  exec_node_logUrl?: string;
  error?: {
    message?: string;
    type?: string
  };
  durationMillis?: string;
  _links?: {
    self?: {
      href?: string
    }
  };
}
