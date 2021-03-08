import { Component, Input, OnInit, Type, ViewChild } from '@angular/core';
import { MatVerticalStepper } from '@angular/material';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';


@Component({
  selector: 'app-build-detail',
  templateUrl: './build-detail.component.html',
  styleUrls: ['./build-detail.component.scss']
})
export class BuildDetailComponent implements OnInit {

  @Input() detailView: Type<any>;
  @ViewChild(MatVerticalStepper, {static: false}) stepper: MatVerticalStepper;


  public data: any[];
  public readableDuration;

  constructor(
    public activeModal: NgbActiveModal,
  ) { }

  ngOnInit() {
  }

  @Input()
  set detailData(data: any) {
    if (data.data) {
      this.data = data.data;
    } else {
      this.data = [data];
    }

    this.data[0].stages.map(stage => {
      if (stage.error) {
        stage.error.message = `${stage.error.message.substring(0, 150)} ...`
      }
    })

    this.readableDuration = this.convertToReadable(this.data[0].duration)

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

  getTooltipInfo(stage){
    let tooltipObj = { 'Status': stage.status, 'Duration (ms)': stage.durationMillis}
    return JSON.stringify(tooltipObj)
  }

  complete() {
    console.log('completed')
    // console.log(this.stepper.selected)
    // this.stepper.selected.completed = true;
    // this.stepper.selected.editable = false;
}
}


// this.data[0].stages[0].status = "FAILURE"
// this.data[0].stages[0].error = {}
// this.data[0].stages[0].error.type = 'java.lang.Error'
// this.data[0].stages[0].error.message = "No such DSL method 'bogieNode' found among steps [ArtifactoryGradleBuild, MavenDescriptorStep, VersionNumber, acceptGitLabMR, addGitLabMRComment, addInteractivePromotion, ansiColor, ansiblePlaybook, archive, artifactoryDistributeBuild, artifactoryDownload, artifactoryEditProps, artifactoryMavenBuild, artifactoryNpmInstall, artifactoryNpmPublish, artifactoryPromoteBuild, artifactoryUpload, awaitDeploymentCompletion, awsIdentity, bat, build, catchError, cfInvalidate, cfnCreateChangeSet, cfnDelete, cfnDeleteStackSet, cfnDescribe, cfnExecuteChangeSet, cfnExports, cfnUpdate, cfnUpdateStackSet, cfnValidate, checkout, checkpoint, collectEnv, conanAddRemote, conanAddUser, container, containerLog, copyRemoteArtifacts, deleteDir, deployAPI, deployArtifacts, dir, dockerFingerprintFrom, dockerFingerprintRun, dockerNode, dockerPullStep, dockerPushStep, ec2ShareAmi, echo, ecrLogin, emailext, emailextrecipients, envVarsForTool, error, fileExists, findFiles, gatlingArchive, getArtifactoryServer, getContext, getImageVulnsFromQualys, git, githubNotify, gitlabBuilds, gitlabCommitStatus, httpRequest, hygieiaArtifactPublishStep, hygieiaBuildPublishStep, hygieiaCodeQualityPublishStep, hygieiaDeployPublishStep, hygieiaMetaDataPublishStep, hygieiaSonarPublishStep, hygieiaTestPublishStep, initConanClient, input, invokeLambda, isUnix, jiraComment, jiraIssueSelector, jiraSearch, junit, kubernetesDeploy, library, libraryResource, listAWSAccounts, load, lock, mail, milestone, newArtifactoryServer, newBuildInfo, newGradleBuild, newMavenBuild, newNpmBuild, nexusPolicyEvaluation, nexusPublisher, node, nodesByLabel, openshiftBuild, openshiftCreateResource, openshiftDeleteResourceByJsonYaml, openshiftDeleteResourceByKey, openshiftDeleteResourceByLabels, openshiftDeploy, openshiftExec, openshiftImageStream, openshiftScale, openshiftTag, openshiftVerifyBuild"

