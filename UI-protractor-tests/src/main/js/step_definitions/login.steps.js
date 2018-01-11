const
    homePage = require('../pages/home.page'),
    loginPage = require('../pages/login.page'),
    listOf = require('../text').listOf,
    expect = require('../expect').expect;

module.exports = function loginSteps() {

    this.setDefaultTimeout(60 * 1000);

    this.Given(/^I navigate to the login page$/, () => {
        homePage.navigateToLoginPage();
    });

    this.Given(/^I am logged in with valid credentials$/, () => {
        homePage.getWelcomeText().then((welcomeText) => {
            expect(welcomeText).to.include("Welcome");
        });
    });

    this.When(/^I select standard login page$/, () => {
        loginPage.clickStandardLogin();
    });

    this.When(/^I enter login credentials (.*) and (.*)$/, (invalidUser, invalidPassword) => {
        loginPage.setUsername(invalidUser);
        loginPage.setPassword(invalidPassword);
    });

    this.When(/^I attempt to login$/, () => {
        loginPage.clickLoginButton();
    });

    this.Then(/^I should be on the login page$/, () => {
        loginPage.isStandardLoginPage().then((result) => {
            expect(result).to.be.true;
        });
    });

    this.Then(/^I should see an error for wrong username or password$/, () => {
        loginPage.getErrorMessage().then((errorMessage) => {
            expect(errorMessage).to.equal("Incorrect username and password");
        });
    });

    this.Then(/^I should be redirected to the home page$/, () => {
        homePage.isHomePage().then((result) => {
            expect(result).to.be.true;
        });
    });

    this.Then(/^the welcome header should contain username (.*)$/, (userName) => {
        homePage.getWelcomeText().then((welcomeText) => {
            expect(welcomeText).to.include(userName.toUpperCase());
        });
    });

    this.Then(/^click on logout$/, () => {
        homePage.clickOnLogoutButton();
    });

    this.Given(/^I login with valid credentials (.*) and (.*)/, (validUser, validPassword) => {
        homePage.navigateToLoginPage();
        loginPage.clickStandardLogin();
        loginPage.setUsername(validUser);
        loginPage.setPassword(validPassword);
        loginPage.clickLoginButton();
    });

};