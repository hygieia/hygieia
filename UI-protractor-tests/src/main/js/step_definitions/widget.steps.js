const
    homePage = require('../pages/home.page'),
    loginPage = require('../pages/login.page'),
    featureWidgetPage = require('../pages/featureWidget.page'),
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

};