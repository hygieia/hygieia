const format = require('util').format;
const log = require('../util/logger');

const BuildWidgetPage = function() {

    const po = this;

    po.buildJob                 =   element(by.name(`collectorItemId`));
    po.buildDurationThreshold   =   element(by.name(`buildDurationThreshold`));
    po.alertTakeoverCriteria    =   element(by.name(`buildConsecutiveFailureThreshold`));
    po.saveButton               =   element(by.css(`[name="buildConfigForm"] .btn`));

    po.setBuildJob = (buildJob) => {
        po.buildJob.sendKeys(buildJob).then(() => {
            log.info(`Set Build Job : ${buildJob}`);
        }, (err) => {
            log.error(`Unable to set build job. ERROR: ${err}`);
        });
    };

    po.setBuildDurationThreshold = (buildDurationThreshold) => {
        po.buildDurationThreshold.sendKeys(buildDurationThreshold).then(() => {
            log.info(`Set Build Duration Threshold : ${buildDurationThreshold}`);
        }, (err) => {
            log.error(`Unable to set build duration threshold. ERROR: ${err}`);
        });
    };

    po.setAlertTakeoverCriteria = (alertTakeoverCriteria) => {
        po.alertTakeoverCriteria.sendKeys(alertTakeoverCriteria).then(() => {
            log.info(`Set Alert Takeover Criteria : ${alertTakeoverCriteria}`);
        }, (err) => {
            log.error(`Unable to set alert takeover criteria. ERROR: ${err}`);
        });
    };

    po.clickSaveButton = () => {
        po.saveButton.click().then(() => {
            log.info(`Click Save Button`);
        }, (err) => {
            log.error(`Unable to click save button. ERROR: ${err}`);
        });
    };

};

module.exports = new BuildWidgetPage();