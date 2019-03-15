export interface BuildResponse {
    result: Build[];
    lastUpdated: number;
}

export interface Build {
    id: string;
    collectorItemId: string;
    timestamp: number;
    number: string;
    buildUrl: string;
    startTime: number;
    endTime: number;
    duration: number;
    buildStatus: string;
    codeRepos: CodeRepo[];
    sourceChangeSet: any[];
}

export interface CodeRepo {
    url: string;
    branch: string;
    type: string;
}
