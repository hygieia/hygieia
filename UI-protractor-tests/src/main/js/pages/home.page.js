const format = require('util').format;
const log = require('../util/logger');
const HomePage = function() {

    const po = this;

    // One responsibility of a traditional Page Object
    // is to define the structure of a page the test will interact with.

    po.loginHeader    =   element(by.cssContainingText('.welcome-header', 'Login'));
    po.welcomeHeader    =   element(by.cssContainingText('.welcome-header', 'Welcome'));

    // -----------------------------------------------------------------------------------------------------------------

    // The second responsibility of a traditional Page Object
    // is to define the interactions a user can have with a specific page the Page Object models.

    po.navigateToLoginPage = () => {
        browser.get('/#/');
        browser.manage().window().maximize();
        po.loginHeader.click().then(() => {
            log.info(`Navigate to Login Page`);
        }, (err) => {
            log.error(`Unable to navigate to Login Page. ERROR: ${err}`);
        });
    };

    po.getWelcomeText = () => {
        return po.welcomeHeader.getText().then((text) => {
            log.info(`Welcome Text : ${text}`);
            return text;
        }, (err) => {
            log.error(`Unable to get Welcome Text. ERROR: ${err}`);
        });
    };

    po.isHomePage = () => {
        return po.welcomeHeader.isDisplayed().then((result) => {
            if (result) {
                log.info(`Home Page displayed`);
                return result;
            } else {
                log.info(`Home Page not displayed`);
                return result;
            }
        }, (err) => {
            log.error(`Unable to locate home page welcome header. ERROR: ${err}`);
        });
    };

};

module.exports = new HomePage();