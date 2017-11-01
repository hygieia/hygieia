const format = require('util').format;
const log = require('../util/logger');
const waitFor = require('../util/waitFor');

const CreateDashboardBubble = function() {

    const po = this;

    po.widgetModalHeading   =   element(by.className(`widget-modal-heading`));
    po.dashboardType        =   element(by.name(`dashboardType`));
    po.selectLayout         =   element(by.css(`.form-group .radio-inline`));
    po.selectWidgets        =   element(by.css(`input[id="widgets"]`));
    po.selectTemplates      =   element(by.css(`input[id="templates"]`));
    po.dashboardTitle       =   element(by.name(`dashboardTitle`));
    po.applicationName      =   element(by.name(`applicationName`));
    po.businessService      =   element(by.name(`configurationItemBusServ`));
    po.businessApplication  =   element(by.name(`configurationItemBusApp`));
    po.createButton         =   element(by.cssContainingText(`.btn.btn-primary`, `Create`));
    po.dashboardHeader      =   element(by.css(`#header h4`));
    po.templateDropdown     =   element(by.name(`selectedTemplate`));
    po.featureWidgetSettings=   element(by.css(`[name="feature"] .widget-body-config .fa`));
    po.buildWidgetSettings  =   element(by.css(`[name="build"] .widget-body-config .fa`));
    po.codeRepoWidgetSettings   =   element(by.css(`[name="repo"] .widget-body-config .fa`));

    po.setDashboardType = (dashboardType) => {
        po.dashboardType.sendKeys(dashboardType).then(() => {
            log.info(`Set Dashboard Type : ${dashboardType}`);
        }, (err) => {
            log.error(`Unable to set dashboard type. ERROR: ${err}`);
        });
    };

    po.setLayout = (layoutType) => {
        element(by.cssContainingText(`.form-group .radio-inline`, layoutType)).click().then(() => {
            log.info(`Set Layout Type : ${layoutType}`);
        }, (err) => {
            log.error(`Unable to set layout type. ERROR: ${err}`);
        });
    };

    po.selectTemplate = (template) => {
        po.templateDropdown.element(by.cssContainingText(`option`, template)).click().then(() => {
            log.info(`Select Template : ${template}`);
        }, (err) => {
            log.error(`Unable to select template. ERROR: ${err}`);
        });
    };

    po.setDashboardTitle = (dashboardTitle) => {
        po.dashboardTitle.sendKeys(dashboardTitle).then(() => {
            log.info(`Set Dashboard Title : ${dashboardTitle}`);
        }, (err) => {
            log.error(`Unable to set dashboard title. ERROR: ${err}`);
        });
    };

    po.setApplicationName = (applicationName) => {
        po.applicationName.sendKeys(applicationName).then(() => {
            log.info(`Set Application Name : ${applicationName}`);
        }, (err) => {
            log.error(`Unable to set application name. ERROR: ${err}`);
        });
    };

    po.setBusinessService = (businessService) => {
        po.businessService.sendKeys(businessService).then(() => {
            log.info(`Set Business Service : ${businessService}`);
        }, (err) => {
            log.error(`Unable to set business service. ERROR: ${err}`);
        });
    };

    po.setBusinessApplication = (businessApplication) => {
        po.businessApplication.sendKeys(businessApplication).then(() => {
            log.info(`Set Business Application : ${businessApplication}`);
        }, (err) => {
            log.error(`Unable to set business application. ERROR: ${err}`);
        });
    };

    po.clickCreate = () => {
        po.createButton.click().then(() => {
            log.info(`Click Create Button`);
        }, (err) => {
            log.error(`Unable to click create button. ERROR: ${err}`);
        });
    };

    po.isCreateDashboardBubble = () => {
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

    po.getDashboardHeader = () => {
        waitFor.elementToBeVisible("Dashboard Header", po.dashboardHeader, 5);
        return po.dashboardHeader.getText().then((headerText) => {
            log.info(`Dashboard Header Text : ${headerText}`);
            return headerText;
        }, (err) => {
            log.error(`Unable to get dashboard name. ERROR: ${err}`);
        });
    };

    po.configureFeatureWidget = () => {
        po.featureWidgetSettings.click().then(() => {
            log.info(`Configure Feature Widget`);
        }, (err) => {
            log.error(`Unable to click on config button for feature widget. ERROR: ${err}`);
        });
    };

    po.configureBuildWidget = () => {
        po.buildWidgetSettings.click().then(() => {
            log.info(`Configure Build Widget`);
        }, (err) => {
            log.error(`Unable to click on config button for build widget. ERROR: ${err}`);
        });
    };

    po.configureCodeRepoWidget = () => {
        po.codeRepoWidgetSettings.click().then(() => {
            log.info(`Configure Code Repo Widget`);
        }, (err) => {
            log.error(`Unable to click on config button for code repo widget. ERROR: ${err}`);
        });
    };
};

module.exports = new CreateDashboardBubble();