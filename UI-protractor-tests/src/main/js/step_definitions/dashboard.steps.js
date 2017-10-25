const
    homePage = require('../pages/home.page'),
    dashboardPage = require('../pages/dashboard.page'),
    deleteConfirmationPage = require('../pages/deleteConfirmation.page'),
    listOf = require('../text').listOf,
    expect = require('../expect').expect;

module.exports = function dashboardSteps() {

    this.setDefaultTimeout(60 * 1000);

    this.When(/^I click on the create dashboard button/, () => {
        homePage.clickOnCreateDashboard();
    });

    this.When(/^I set the dashboard type (.*)/, (dashboardType) => {
        dashboardPage.setDashboardType(dashboardType)
    });

    this.When(/^I set the layout type (.*)/, (layoutType) => {
        dashboardPage.setLayout(layoutType);
    });

    this.When(/^I select a template (.*)/, (template) => {
        dashboardPage.selectTemplate(template);
    });

    this.When(/^I set the dashboard title (.*)/, (dashboardName) => {
        dashboardPage.setDashboardTitle(dashboardName);
    });

    this.When(/^I set the application name (.*)/, (applicationName) => {
        dashboardPage.setApplicationName(applicationName)
    });

    this.When(/^I create the dashboard/, () => {
        dashboardPage.clickCreate();
    });

    this.Given(/^I navigate to home page/, () => {
        homePage.navigateToHomePage();
    });

    this.When(/^I click on delete button for (.*)/, (dashboardName) => {
        homePage.clickOnDeleteDashboard(dashboardName);
    });

    this.When(/^I confirm delete/, () => {
        deleteConfirmationPage.confirmDelete();
    });

    this.Then(/^the dashboard (.*) should be deleted/, (dashboardName) => {
        homePage.getMyDashboardList().then((allMyDashboards) => {
            expect(allMyDashboards).not.to.include(dashboardName);
        });
    });

    // this.Then(/^verify the new dashboard (.*) is created/, (dashboardName) => {
    //     expect(homePage.getMyDashboardList()).to.include(dashboardName);
    // });

    this.Then(/^the current dashboard header should read (.*)/, (dashboardName) => {
        dashboardPage.getDashboardHeader().then((headerText) => {
            expect(headerText).to.include(dashboardName);
        });
    });

};