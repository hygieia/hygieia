const format = require('util').format;
const log = require('../util/logger');

const LoginPage = function() {

    const po = this;

    po.usernameInput    =   element(by.name('username'));
    po.passwordInput    =   element(by.name('password'));
    po.loginButton      =   element(by.tagName('button'));
    po.helpBlock        =   element(by.className('help-block'));
    po.standardLoginSection     =   element(by.css('[ng-show="isStandardLogin()"]'));
    po.ldapLoginSection =   element(by.css('[ng-show="isLdapLogin()"]'));
    po.standardLogin    =   element(by.css('[ng-click="showStandard()"]'));
    po.ldapLogin        =   element(by.css('[ng-click="showLdap()"]'));


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

    po.clickLoginButton = () => {
        po.loginButton.click().then(() => {
            log.info(`Click on Login Button`);
        }, (err) => {
            log.error(`Unable to click on Login Button. ERROR: ${err}`);
        });
    };

    po.clickStandardLogin = () => {
        po.standardLogin.click().then(() => {
            log.info(`Click on Standard Login`);
        }, (err) => {
            log.error(`Unable to click Standard Login`);
        });
    };

    po.clickLdapLogin = () => {
        po.ldapLogin.click().then(() => {
            log.info(`Click on LDAP Login`);
        }, (err) => {
            log.error(`Unable to click LDAP Login`);
        });
    };

    po.isStandardLoginPage = () => {
        return po.standardLoginSection.isDisplayed().then((result) => {
            if (result) {
                log.info(`Standard Login Page displayed`);
                return result;
            } else {
                log.info(`Standard Login Page not displayed`);
                return result;
            }
        }, (err) => {
            log.error(`Unable to locate standard login section. ERROR: ${err}`);
        });
    };

    po.isLdapLoginPage = () => {
        return po.ldapLoginSection.isDisplayed().then((result) => {
            if (result) {
                log.info(`LDAP Login Page displayed`);
                return result;
            } else {
                log.info(`LDAP Login Page not displayed`);
                return result;
            }
        }, (err) => {
            log.error(`Unable to locate ldap login section. ERROR: ${err}`);
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