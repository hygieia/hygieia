#!/usr/bin/env groovy

import groovy.json.JsonSlurperClassic

// The class provide alternate solution to HygieiaJenkinsPlugin. 
// An enterprise might restrict installation of Hygieia Jenkins Plungin for a number of reasons - compliance, stability, governance, SOX etc...
// HygieiaJenkinsPluginAlt is called by Jenkins pipeline, it capture metrics (collected by Hygieia Jenkins plugin)  
// and push it to Hygieia by calling Hygieia API's to persist data in Hygieia Mongo database

String buildIdJenkins = ''
scmCommitDeveloper = ""
scmFileChangedDescription = ""
noOfFilesChanged = ""

def getScmMetrics(jenkinsBuildUrl, authtoken) {
	jenkinsBuildUrl = jenkinsBuildUrl + "/api/json"
	
	def response = httpRequest customHeaders: [[name: 'Authorization', value: authtoken]], url: jenkinsBuildUrl
    if (response.status >= 200 && response.status < 300) {
        response.content
    } else {
        error "Failure: Unable to get HTTP resource.  Status is $response.status"
    }

    def payloadData = parseJson(response.content)
    
	if (payloadData.changeSets.items.author.fullName[0]!=null) {
        def developer = payloadData.changeSets.items.author.fullName[0]
        scmCommitDeveloper = developer[0]
    }
    
    if (payloadData.changeSets.items.msg[0]!=null) {
        def changedDescription = payloadData.changeSets.items.msg[0]
        scmFileChangedDescription = changedDescription[0]
    }
	
    if (payloadData.changeSets.items.affectedPaths[0]!=null) {
        def fileChanged = payloadData.changeSets.items.affectedPaths.size()
        noOfFilesChanged = fileChanged
    }
}


def parseJson(text) {
    return new JsonSlurperClassic().parseText(text)
}

// Function to post deployment metrics to Hygieia
def postMetrics(authtoken, hygieiaApiUrl, number, buildUrl, jobName, buildStatus, startTime, jobUrl, instanceUrl, endTime, duration, url, branch, type, scmRevisionNumber, scmCommitTimestamp, artifactName, canonicalName, artifactGroup, artifactVersion, artifactModule, artifactExtension, buildId, timestamp, appName, envName, deployStatus) {
	getScmMetrics(buildUrl, authtoken)
	postHygieiaBuildMetrics(hygieiaApiUrl, number, buildUrl, jobName, buildStatus, startTime, jobUrl, instanceUrl, endTime, duration, url, branch, type, scmRevisionNumber, scmCommitTimestamp)
	postHygieiaArtifactMetrics(hygieiaApiUrl, artifactName, canonicalName, artifactGroup, artifactVersion, artifactModule, artifactExtension, buildId, timestamp, scmRevisionNumber, scmCommitTimestamp, jobName, url, branch, buildUrl, jobUrl, number, instanceUrl)
	postHygieiaDeployMetrics(hygieiaApiUrl, number, jobUrl, appName, envName, artifactName, artifactVersion, jobName, instanceUrl, deployStatus, startTime, endTime, duration)
}


def postHygieiaBuildMetrics(hygieiaApiUrl, number, buildUrl, jobName, buildStatus, startTime, jobUrl, instanceUrl, endTime, duration, giturl, branch, type, scmRevisionNumber, scmCommitTimestamp) {
	def customHeaders = [[name: 'Content-Type', value: 'application/json']]
	hygieiaApiUrl = hygieiaApiUrl + "build"
	def contents = """
	{  
	   "number":"$number",
	   "buildUrl":"$buildUrl",
	   "jobName":"$jobName",
	   "buildStatus":"$buildStatus",
	   "startTime": "$startTime",
	   "jobUrl":"$jobUrl",
	   "instanceUrl":"$instanceUrl",
	   "niceName":"$jobName",
	   "endTime":"$endTime",
	   "duration":"$duration",
	   "codeRepos":[  
		  {  
			 "url":"$giturl",
			 "branch":"$branch",
			 "type":"$type"
		  }
	   ],
	   "sourceChangeSet":[  
		  {  
			 "scmRevisionNumber":"$scmRevisionNumber",
			 "scmCommitLog":"$scmChangedDescription",
			 "scmAuthor":"$scmCommitDeveloper",
			 "scmCommitTimestamp":"$scmCommitTimestamp",
			 "numberOfChanges":"$noOfFilesChanged"
		  }
	   ]
	}"""
    def response = httpRequest httpMode: 'POST', requestBody: contents, customHeaders: customHeaders, url: hygieiaApiUrl
  	if (response.status >= 200 && response.status < 300) {
      	buildIdJenkins=response.content
      	buildIdJenkins=buildIdJenkins.replace("\"", "")
 	    return response.content
	} else {
	    error "Failure: Unable to get HTTP resource.  Status is $response.status"
	}
}


def postHygieiaArtifactMetrics(hygieiaApiUrl, artifactName, canonicalName, artifactGroup, artifactVersion, artifactModule, artifactExtension, buildId, timestamp, scmRevisionNumber, scmCommitTimestamp, jobName, scmUrl, scmBranch, buildUrl, jobUrl, buildNumber, instanceUrl){
	def customHeaders = [[name: 'Content-Type', value: 'application/json']]
    hygieiaApiUrl = hygieiaApiUrl + "artifact"
    def contents = """
	{  
	   "artifactName":"$artifactName",
	   "canonicalName":"$canonicalName",
	   "artifactGroup":"$artifactGroup",
	   "artifactVersion":"$artifactVersion",
	   "artifactModule":"$artifactModule",
	   "artifactExtension":"$artifactExtension",
	   "buildId":"$buildIdJenkins",
	   "timestamp":"$timestamp",
	   "sourceChangeSet":[  
		  {  
			 "scmRevisionNumber":"$scmRevisionNumber",
			 "scmCommitLog":"$scmChangedDescription",
			 "scmAuthor":"$scmCommitDeveloper",
			 "scmCommitTimestamp":"$scmCommitTimestamp",
			 "numberOfChanges":"$noOfFilesChanged"
		  }
	   ],
	   "metadata":{  
		  "jobName":"$jobName",
		  "scmUrl":"$scmUrl",
		  "scmBranch":"$scmBranch",
		  "buildUrl":"$buildUrl",
		  "jobUrl":"$jobUrl",
		  "buildNumber":"$buildNumber",
		  "scmRevisionNumber":"$scmRevisionNumber",
		  "instanceUrl":"$instanceUrl"
	   }
	}"""
    def response = httpRequest httpMode: 'POST', requestBody: contents, customHeaders: customHeaders, url: hygieiaApiUrl
    if (response.status >= 200 && response.status < 300) {
	    return response.content
	} else {
	    error "Failure: Unable to get HTTP resource.  Status is $response.status"
	}
}


def postHygieiaDeployMetrics(hygieiaApiUrl, number, jobUrl, appName, envName, artifactName, artifactVersion, jobName, instanceUrl, deployStatus, startTime, endTime, duration) {
	def customHeaders = [[name: 'Content-Type', value: 'application/json']]
	hygieiaApiUrl = hygieiaApiUrl + "deploy"
    def contents = """
	{  
	   "executionId":"$number",
	   "jobUrl":"$jobUrl",
	   "appName":"$appName",
	   "envName":"$envName",
	   "artifactName":"$artifactName",
	   "artifactVersion":"$artifactVersion",
	   "jobName":"$jobName",
	   "instanceUrl":"$instanceUrl",
	   "deployStatus":"$deployStatus",
	   "startTime":"$startTime",
	   "hygieiaId":"$buildIdJenkins",
	   "endTime":"$endTime",
	   "duration":"$duration",
	   "niceName":"$jobName"
	}"""
    def response = httpRequest httpMode: 'POST', requestBody: contents, customHeaders: customHeaders, url: hygieiaApiUrl
    if (response.status >= 200 && response.status < 300) {
	  return response.content
    } else {
	    error "Failure: Unable to get HTTP resource.  Status is $response.status"
	}
}

return this;