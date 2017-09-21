const format = require('util').format;
const log = require('../util/logger');
const LoginPage = function() {

    const po = this;

    // One responsibility of a traditional Page Object
    // is to define the structure of a page the test will interact with.

    po.usernameInput    =   element(by.name('username'));

    po.passwordInput    =   element(by.name('password'));

    po.loginButton      =   element(by.tagName('button'));

    po.helpBlock        =   element(by.className('help-block'));

    // -----------------------------------------------------------------------------------------------------------------

    // The second responsibility of a traditional Page Object
    // is to define the interactions a user can have with a specific page the Page Object models.

    po.setUsername = (username) => {
        po.usernameInput.sendKeys(username).then((username) => {
            log.info(`Set Username : ${username}`);
        }, (err) => {
            log.error(`Unable to set username. Error: ${err}`);
        });
    };

    po.setPassword = (password) => {
        po.passwordInput.sendKeys(password).then((password) => {
            log.info(`Set Password : ${password}`);
        }, (err) => {
            log.error(`Unable to set password. Error: ${err}`);
        })
    };

    po.login = () => {
        po.loginButton.click().then(() => {
            log.info(`Click on Login Button`);
        }, (err) => {
            log.error(`Unable to click on Login Button. Error: ${err}`);
        });
    };

};

module.exports = new LoginPage();