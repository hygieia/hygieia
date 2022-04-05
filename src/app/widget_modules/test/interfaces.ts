export interface ITest {
    result: any;
    description: string;
    timestamp: number;
    executionId: string;
    duration: number;
    totalCount: number;
    successCount: number;
    failureCount: number;
    startTime: number;
    endTime: number;
    url: string;
    type: TestType;
    collectorItemId: string;
    testCapabilities: any;
}

export enum TestType {
    Functional = 'Functional',
    Performance = 'Performance',
}

