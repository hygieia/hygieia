import { Component, OnInit, } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BuildService } from '../build.service';
import { IBuild } from '../interfaces';


@Component({
  selector: 'app-build-detail',
  templateUrl: './build-detail-page.component.html',
  styleUrls: ['./build-detail-page.component.scss']
})
export class BuildDetailPageComponent implements OnInit {
  public buildId: string;
  public data: IBuild;
  public readableDuration;
  public buildStatusArray = [`Success`, `Failed`, `Failure`, `Aborted`];
  public stageStatusArray = [`SUCCESS`, `FAILED`, `FAILURE`, `ABORTED`, `NOT_EXECUTED`];

  constructor(
    private route: ActivatedRoute,
    private buildService: BuildService
  ) { }

  ngOnInit() {
    this.buildId = this.route.snapshot.paramMap.get('id');

    this.buildService.fetchBuild(this.buildId).subscribe(res => {
      this.data = res;

      // Truncate error messages
      this.data.stages.map(stage => {
        if (stage.error) {
          stage.error.message = `${stage.error.message.substring(0, 150)} ...`;
        }
      });


      if (this.data.duration) {
        this.readableDuration = this.convertToReadable(this.data.duration);
      } else {
        this.readableDuration = '-- : -- : --';
      }
    });
  }

  convertToReadable(timeInMiliseconds): string {
    const hours = Math.floor(timeInMiliseconds / 1000 / 60 / 60);
    let hoursString = hours.toString();
    if (hours < 10) {
      hoursString = `0${hours.toString()}`;
    }

    const minutes = Math.floor((timeInMiliseconds / 1000 / 60 / 60 - hours) * 60);
    let minutesString = minutes.toString();
    if (minutes < 10) {
      minutesString = `0${minutes.toString()}`;
    }

    const seconds = Math.floor(((timeInMiliseconds / 1000 / 60 / 60 - hours) * 60 - minutes) * 60);
    let secondsString = seconds.toString();
    if (seconds < 10) {
      secondsString = `0${seconds.toString()}`;
    }
    return `${hoursString}:${minutesString}:${secondsString}`;
  }

  getTooltipInfo(stage) {
    const tooltipObj = { Status: stage.status, 'Duration (ms)': stage.durationMillis };
    return JSON.stringify(tooltipObj);
  }

  buildStatusCheck(status: string): boolean {
    return this.buildStatusArray.includes(status);
  }

  stageStatusCheck(status: string): string {
    if (this.stageStatusArray.includes(status)) {
      return status;
    }
    return `default`;
  }
}
