const format = require('util').format;
const log = require('../util/logger');
const waitFor = require('../util/waitFor');
const HomePage = function() {

    const po = this;

    po.loginHeader      =   element(by.cssContainingText('.welcome-header', 'Login'));
    po.welcomeHeader    =   element(by.cssContainingText('.welcome-header', 'Welcome'));
    po.createDashboardButton    =   element(by.className(`create-dashboard-button`));
    po.dashboardSearch  =   element(by.id(`filter`));
    po.myDashboards     =   element.all(by.css(`#myDashboardsSection .list-group-item`));
    po.logoutIcon       =   element(by.css(`.welcome-header .fa-power-off`));
    po.dashboardLogo    =   element(by.className(`dashboard-logo`));

    po.navigateToLoginPage = () => {
        browser.get('/#/');
        // browser.manage().window().maximize();
        // browser.manage().window().setSize(1680, 1024);
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

    po.clickOnCreateDashboard = () => {
        po.createDashboardButton.click().then(() => {
            log.info(`Open create dashboard bubble`);
        }, (err) => {
            log.error(`Unable to locate create dashboard button. ERROR: ${err}`);
        });
    };

    po.searchDashboard = (dashboardName) => {
        po.dashboardSearch.sendKeys(dashboardName).then(() => {
            log.info(`Set Search String : ${dashboardName}`);
        }, (err) => {
            log.error(`Unable to set search string. ERROR: ${err}`);
        });
    };

    po.getMyDashboardList = () => {
        return po.myDashboards.getText().then((allMyDashboards) => {
            log.info(`All My Dashboards : ${allMyDashboards}`);
            return allMyDashboards;
        }, (err) => {
            log.error(`Unable to get all my dashboards. ERROR: ${err}`);
        });
    };

    po.selectDashboard = (dashboardName) => {
        po.myDashboards.filter((elem) => {
            return elem.getText().then((text) => {
                return text.includes(dashboardName);
            });
        }).first().click().then(() => {
            log.info(`Select Dashboard : ${dashboardName}`);
        }, (err) => {
            log.error(`Unable to select dashboard. ERROR: ${err}`);
        });
    };

    po.clickOnDeleteDashboard = (dashboardName) => {
        po.myDashboards.filter((elem) => {
            return elem.getText().then((text) => {
                return text.includes(dashboardName);
            }, (err) => {
                log.info(`Unable to get dashboard name: ERROR: ${err}`);
            });
        }, (err) => {
            log.info(`Unable to filter elements: ERROR: ${err}`);
        }).first().element(by.id(`deleteDashboardButton`)).click().then(() => {
            log.info(`Click on Delete Dashboard Button for : ${dashboardName}`);
        }, (err) => {
            log.error(`Unable to click on delete dashboard: ERROR: ${err}`);
        });
    };

    po.clickOnLogoutButton = () => {
        browser.sleep(3000);
        po.logoutIcon.click().then(() => {
            log.info(`Logging Out`);
        }, (err) => {
            log.error(`Unable to click on logout icon. ERROR: ${err}`);
        });
    }

};

module.exports = new HomePage();