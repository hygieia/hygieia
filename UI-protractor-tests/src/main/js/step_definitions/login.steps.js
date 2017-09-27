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

    this.When(/^I enter login credentials (.*) and (.*)$/, (invalidUser, invalidPassword) => {
        loginPage.setUsername(invalidUser);
        loginPage.setPassword(invalidPassword);
    });

    this.When(/^I attempt to login$/, () => {
        loginPage.clickLogin();
    });


    this.Then(/^I should be on the login page$/, () => {
        loginPage.isLoginPage().then((result) => {
            expect(result).to.be.true;
        });
    });

    this.Then(/^I should see an error for wrong username or password$/, () => {
        loginPage.getErrorMessage().then((errorMessage) => {
            expect(errorMessage).to.equal("Incorrect username and password");
        });
    });

    this.Then(/^I should be redirected to the home page$/, () => {

    });

    this.Then(/^the welcome header should contain username (.*)$/, (userName) => {
        homePage.getWelcomeText().then((welcomeText) => {
            expect(welcomeText).to.include(userName.toUpperCase());
        });
    });

};