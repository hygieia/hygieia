import { Build } from './interfaces';

export default class BuildDataParser {
    static buildsPerDay(data: Build[]): any {
        const fifteenDays = this.toMidnight(new Date());
        fifteenDays.setDate(fifteenDays.getDate() - 14);
    }

    private static toMidnight(date: Date): Date {
        date.setHours(0, 0, 0, 0);
        return date;
    }

    private static afterDate(build: Build, date: Date): boolean {
        return build.endTime >= date.getTime();
    }

    private static isStatus(build: Build, status: string): boolean {
        return build.buildStatus === status;
    }
}
