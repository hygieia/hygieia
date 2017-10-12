const
    homePage = require('../pages/home.page'),
    createDashboardBubble = require('../pages/dashboard.page'),
    listOf = require('../text').listOf,
    expect = require('../expect').expect;

module.exports = function dashboardSteps() {

    this.setDefaultTimeout(60 * 1000);

    this.Given(/^I click on the create dashboard button/, () => {
        homePage.clickOnCreateDashboard();
    });

    this.Given(/^I set the dashboard type (.*)/, (dashboardType) => {
        createDashboardBubble.setDashboardType(dashboardType)
    });

    this.Given(/^I set the layout type (.*)/, (layoutType) => {
        createDashboardBubble.setLayout(layoutType);
    });

    this.Given(/^I select a template (.*)/, (template) => {
        createDashboardBubble.selectTemplate(template);
    });

    this.Given(/^I set the dashboard title (.*)/, (dashboardName) => {
        createDashboardBubble.setDashboardTitle(dashboardName);
    });

    this.Given(/^I set the application name (.*)/, (applicationName) => {
        createDashboardBubble.setApplicationName(applicationName)
    });

    this.Given(/^I create the dashboard/, () => {
        createDashboardBubble.clickCreate();
    });

    this.Given(/^I navigate to home page/, () => {
        homePage.navigateToHomePage();
    });

    this.Then(/^verify the new dashboard (.*) is created/, (dashboardName) => {
        expect(homePage.getMyDashboardList()).to.contain(dashboardName);
    });

    this.Then(/^the current dashboard header should read (.*)/, (dashboardName) => {
        homePage.selectDashboard(dashboardName).then(() => {
            expect(homePage.getDashboardHeader()).to.equal(dashboardName);
        });

        homePage.logout();
    });

};