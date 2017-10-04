const format = require('util').format;
const log = require('../util/logger');
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

    po.setDashboardType = (dashboardType) => {
        po.dashboardType.sendKeys(dashboardType).then(() => {
            log.info(`Set Dashboard Type : ${dashboardType}`);
        }, (err) => {
            log.error(`Unable to set dashboard type. ERROR: ${err}`);
        });
    };

    po.setLayout = (layoutType) => {
        element(by.cssContainingText(po.selectLayout, layoutType)).click().then(() => {
            log.info(`Set Layout Type : ${layoutType}`);
        }, (err) => {
            log.error(`Unable to set layout type. ERROR: ${err}`);
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

};

module.exports = new CreateDashboardBubble();