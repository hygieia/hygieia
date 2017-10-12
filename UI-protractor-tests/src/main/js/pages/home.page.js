const format = require('util').format;
const log = require('../util/logger');
const HomePage = function() {

    const po = this;

    po.loginHeader      =   element(by.cssContainingText('.welcome-header', 'Login'));
    po.welcomeHeader    =   element(by.cssContainingText('.welcome-header', 'Welcome'));
    po.createDashboardButton    =   element(by.className(`create-dashboard-button`));
    po.dashboardSearch  =   element(by.id(`filter`));
    po.myDashboards     =   element(by.id(`myDashboardsSection`)).all(by.className(`list-group-item`));
    po.logoutIcon       =   element(by.css(`.welcome-header .fa-power-off`));
    po.dashboardLogo    =   element(by.className(`dashboard-logo`));

    po.navigateToLoginPage = () => {
        browser.get('/#/');
        browser.manage().window().maximize();
        po.loginHeader.click().then(() => {
            log.info(`Navigate to Login Page`);
        }, (err) => {
            log.error(`Unable to navigate to Login Page. ERROR: ${err}`);
        });
    };

    po.navigateToHomePage = () => {
        po.dashboardLogo.click().then(() => {
            log.info(`Navigate to Dashboard Home Page`);
        }, (err) => {
            log.error(`Unable to navigate to home page. ERROR: ${err}`);
        });
    }

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

    po.clickOnCreateDashboard = () => {
        po.createDashboardButton.click().then(() => {
            log.info(`Open create dashboard bubble`);
        }, (err) => {
            log.error(`Unable to locate create dashboard button. ERROR: ${err}`);
        });
    };

    po.searchDashboard = (dashboardName) => {
        po.dashboardSearch.sendKeys(dashboardName).than(() => {
            log.info(`Set Search String : ${dashboardName}`);
        }, (err) => {
            log.error(`Unable to set search string. ERROR: ${err}`);
        });
    };

    po.getMyDashboardList = () => {
        return po.myDashboardList.each((element) => {
            element.getText().then(function (text) {
                log.info(`Dashboard Name: ${text}`);
            }, (err) => {
                log.error(`Unable to get dashboard name. ERROR: ${err}`);
            });
        });
    };

    po.selectDashboard = (dashboardName) => {
        po.myDashboardList.filter(function(elem) {
            return elem.getText().then(function(text) {
                return text === dashboardName;
            });
        }).first().click().then(() => {
            log.info(`Select Dashboard : ${dashboardName}`);
        }, (err) => {
            log.error(`Unable to select dashboard. ERROR: ${err}`);
        });
    };

    po.getDashboardHeader = () => {
        po.dashboardHeader.getText().then((dashboardName) => {
            log.info(`Dashboard Name : ${dashboardName}`);
        }, (err) => {
            log.error(`Unable to get dashboadr name. ERROR: ${err}`);
        });
    };

    po.logout = () => {
        po.logoutIcon.click().then(() => {
            log.info(`Logging Out`);
        }, (err) => {
            log.error(`Unable to click on logout icon. ERROR: ${err}`);
        });
    }

};

module.exports = new HomePage();