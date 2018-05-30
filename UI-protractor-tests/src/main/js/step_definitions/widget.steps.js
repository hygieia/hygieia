const
    homePage = require('../pages/home.page'),
    loginPage = require('../pages/login.page'),
    featureWidgetPage = require('../pages/featureWidget.page'),
    buildWidgetPage = require('../pages/buildWidget.page'),
    coderepoWidgetPage = require('../pages/coderepoWidget.page'),
    listOf = require('../text').listOf,
    expect = require('../expect').expect;

module.exports = function widgetSteps() {

    this.setDefaultTimeout(60 * 1000);

    this.When(/^I select Agile Content Tool Type "(.*?)"/, (agileContentToolType) => {
        featureWidgetPage.selectAgileContentToolType(agileContentToolType);
    });

    this.When(/^I enter a Project Name "(.*?)"/, (projectName) => {
        featureWidgetPage.setProjectName(projectName);
    });

    this.When(/^I enter a Team Name "(.*?)"/, (teamName) => {
        featureWidgetPage.setTeamName(teamName);
    });

    this.When(/^I select Estimate Metric "(.*?)"/, (estimateMetric) => {
        featureWidgetPage.selectEstimateMetric(estimateMetric);
    });

    this.When(/^I select Sprint Type "(.*?)"/, (sprintType) => {
        featureWidgetPage.selectSprintType(sprintType);
    });

    this.When(/^I select List Feature Type "(.*?)"/, (listFeatureType) => {
        featureWidgetPage.selectListFeatureType(listFeatureType);
    });

    this.When(/^I click on Save button/, () => {
        featureWidgetPage.clickSaveButton();
    });

    this.Then(/^the feature widget should display the Project Name "(.*?)"/, (projectName) => {
        featureWidgetPage.getProjectName().then((displayedProjectName) => {
            expect(displayedProjectName).to.equal(projectName);
        });
    });

    // Step Definitions for Build Widget

    this.When(/^I set a Build Job "(.*?)"/, (buildJob) => {
        buildWidgetPage.setBuildJob(buildJob);
    });

    this.When(/^I set Build duration threshold "(.*?)"/, (buildDurationThreshold) => {
        buildWidgetPage.setBuildDurationThreshold(buildDurationThreshold);
    });

    this.When(/^I set alert takeover criteria "(.*?)"/, (alertTakeoverCriteria) => {
        buildWidgetPage.setAlertTakeoverCriteria(alertTakeoverCriteria);
    });

    this.Then(/^the build widget should display the latest builds label "(.*?)"/, (buildsLabel) => {
        buildWidgetPage.getLatestBuildsLabel().then((text) => {
            expect(text).to.equal(buildsLabel);
        });
    });

    this.Then(/^the build widget should display the total builds label "(.*?)"/, (buildsLabel) => {
        buildWidgetPage.getTotalBuildsLabel().then((text) => {
            expect(text).to.equal(buildsLabel);
        });
    });

    // Step Definitions for Code Repo Widget

    this.When(/^I set a repo type "(.*?)"/, (repoType) => {
        coderepoWidgetPage.selectRepoType(repoType);
    });

    this.When(/^I set a repo url "(.*?)"/, (repoURL) => {
        coderepoWidgetPage.setRepoURL(repoURL);
    });

    this.When(/^I set a branch "(.*?)"/, (gitBranch) => {
        coderepoWidgetPage.setBranch(gitBranch);
    });

    this.Then(/^the coderepo widget should display the issues label "(.*?)"/, (issuesLabel) => {
        coderepoWidgetPage.getIssuesLabel().then((text) => {
            expect(text).to.equal(issuesLabel);
        });
    });

};