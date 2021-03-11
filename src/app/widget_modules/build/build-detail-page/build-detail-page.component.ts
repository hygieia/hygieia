import { Component, Input, OnInit, Type, ViewChild } from '@angular/core';
import { MatVerticalStepper } from '@angular/material';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { switchMap } from 'rxjs/operators';
import { BuildDetailComponent } from '../build-detail/build-detail.component';
import { BuildService } from '../build.service';
import { IBuild } from '../interfaces';


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
  ){}

  ngOnInit(){
    this.buildId = this.route.snapshot.paramMap.get('id');
    this.loadBuild(this.buildId);
  }

  private loadBuild(buildId: string){
    this.buildService.fetchBuild(this.buildId).subscribe(res => this.data = res);
    this.readableDuration = this.convertToReadable(this.data.duration);
    // get data based on id 
    // is this from backend as a api call, or do i extract that data from the existing one in service
    // then just copy over the functions from BuildDetailComponent
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
}
