import { Component, Input, OnInit, Type, ViewChild } from '@angular/core';
import { MatVerticalStepper } from '@angular/material';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { switchMap } from 'rxjs/operators';
import { BuildDetailComponent } from '../build-detail/build-detail.component';
import { BuildService } from '../build.service';
import { IBuild, IStage } from '../interfaces';


@Component({
  selector: 'app-build-detail',
  templateUrl: './build-detail-page.component.html',
  styleUrls: ['./build-detail-page.component.scss']
})
export class BuildDetailPageComponent implements OnInit {
  private buildId: string;
  private data: IBuild;
  private readableDuration;

  constructor(
    private route: ActivatedRoute,
    private buildService: BuildService
  ){
    this.data = {
      id: 'buildTitle',
      buildUrl: 'buildUrl',
      buildStatus: "Aborted",
      startTime: 21321312312,
      endTime: 2312312431423424,
      duration: 7030,
      stages: [
        {
          stageId: '111',
          name: 'Test Stage',
          startTimeMillis: '1111',
          durationMillis: '1000',
          _links: {
            self: {
              href: 'url-string'
            }
          }
        }
      ]
    };
  }

  ngOnInit(){
    this.buildId = this.route.snapshot.paramMap.get('id');
    console.log(this.data);
    this.readableDuration = this.convertToReadable(this.data.duration);
    // this.buildService.fetchBuild(this.buildId).subscribe(res => {
    //   this.data = res
    //   console.log(this.data);
    //   if(this.data.duration){
    //     this.readableDuration = this.convertToReadable(this.data.duration);
    //   }
    //   console.log(this.readableDuration);
    // });
  }

  convertToReadable(timeInMiliseconds): String {
    let hours = Math.floor(timeInMiliseconds / 1000 / 60 / 60);
    let hoursString = hours.toString();
    if(hours < 10){
      hoursString = `0${hours.toString()}`
    }

    let minutes = Math.floor((timeInMiliseconds / 1000 / 60 / 60 - hours) * 60);
    let minutesString = minutes.toString();
    if(minutes < 10){
      minutesString = `0${minutes.toString()}`
    }

    let seconds = Math.floor(((timeInMiliseconds / 1000 / 60 / 60 - hours) * 60 - minutes) * 60);
    let secondsString = seconds.toString();
    if(seconds < 10){
      secondsString = `0${seconds.toString()}`
    }
    return `${hoursString}:${minutesString}:${secondsString}`
  }

  getTooltipInfo(stage) {
    const tooltipObj = { Status: stage.status, 'Duration (ms)': stage.durationMillis };
    return JSON.stringify(tooltipObj);
  }

  getURL(stage: IStage){
    //this.data.baseLogUrl + stage._links.self.href
    console.log(stage)
    if(this.data.buildUrl){
      let baseLogUrl = this.data.buildUrl.split('/job')[0];
      if(stage){
        if(stage._links){
          if(stage._links.self){
            if(stage._links.self.href){
              console.log("hi: " + baseLogUrl + stage._links.self.href)
              return baseLogUrl + stage._links.self.href
            }
          }
        }
      }
    }
    return "google.com";
  }
}
