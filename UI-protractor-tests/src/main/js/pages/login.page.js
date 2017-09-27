const format = require('util').format;
const log = require('../util/logger');

const LoginPage = function() {

    const po = this;


    po.usernameInput    =   element(by.name('username'));

    po.passwordInput    =   element(by.name('password'));

    po.loginButton      =   element(by.tagName('button'));

    po.helpBlock        =   element(by.className('help-block'));

    po.loginSection     =   element(by.css('[ng-show="isStandardLogin()"]'));


    po.setUsername = (username) => {
        po.usernameInput.sendKeys(username).then(() => {
            log.info(`Set Username : ${username}`);
        }, (err) => {
            log.error(`Unable to set username. ERROR: ${err}`);
        });
    };

    po.setPassword = (password) => {
        po.passwordInput.sendKeys(password).then(() => {
            log.info(`Set Password : ${password}`);
        }, (err) => {
            log.error(`Unable to set password. ERROR: ${err}`);
        })
    };

    po.clickLogin = () => {
        po.loginButton.click().then(() => {
            log.info(`Click on Login Button`);
        }, (err) => {
            log.error(`Unable to click on Login Button. ERROR: ${err}`);
        });
    };

    po.isLoginPage = () => {
        return po.loginSection.isDisplayed().then((result) => {
            if (result) {
                log.info(`Login Page displayed`);
                return result;
            } else {
                log.info(`Login Page not displayed`);
                return result;
            }
        }, (err) => {
            log.error(`Unable to locate login section. ERROR: ${err}`);
        });
    };

    po.getErrorMessage = () => {
        return po.helpBlock.getText().then((text) => {
            log.info(`Error Message : ${text}`);
            return text;
        }, (err) => {
            log.error(`Unable to get Error Message. ERROR: ${err}`);
        });
    };

};

module.exports = new LoginPage();