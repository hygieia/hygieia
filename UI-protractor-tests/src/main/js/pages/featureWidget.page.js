const format = require('util').format;
const log = require('../util/logger');

const FeatureWidgetPage = function() {

    const po = this;

    po.featureDataSourceDropDown    =   element(by.name(`collectorId`));
    po.projectName                  =   element(by.name(`projectName`));
    po.teamName                     =   element(by.name(`teamName`));
    po.estimateMetricType           =   element(by.name(`estimateMetricType`));
    po.sprintType                   =   element(by.name(`sprintType`));
    po.listFeatureType              =   element(by.name(`listType`));
    po.saveButton                   =   element(by.css(`[name="configForm"] .btn`));

    po.selectFeatureDataSource = (datasource) => {
        po.featureDataSourceDropDown.element(by.cssContainingText(`option`, datasource)).click().then(() => {
            log.info(`Select Feature Data Source : ${datasource}`);
        }, (err) => {
            log.error(`Unable to select feature data source. ERROR: ${err}`);
        });
    };

    po.setProjectName = (projectName) => {
        po.projectName.sendKeys(projectName).then(() => {
            log.info(`Set Project Name : ${projectName}`);
        }, (err) => {
            log.error(`Unable to set project name. ERROR: ${err}`);
        });
    };

    po.setTeamName = (teamName) => {
        po.teamName.sendKeys(teamName).then(() => {
            log.info(`Set Team Name : ${teamName}`);
        }, (err) => {
            log.error(`Unable to set team name. ERROR: ${err}`);
        });
    };

    po.selectEstimateMetric = (estimateMetric) => {
        po.estimateMetricType.element(by.cssContainingText(`option`, estimateMetric)).click().then(() => {
            log.info(`Select Estimate Metric : ${estimateMetric}`);
        }, (err) => {
            log.error(`Unable to select estimate metric. ERROR: ${err}`);
        });
    };

    po.selectSprintType = (sprintType) => {
        po.sprintType.element(by.cssContainingText(`option`, sprintType)).click().then(() => {
            log.info(`Select Sprint Type : ${sprintType}`);
        }, (err) => {
            log.error(`Unable to select sprint type. ERROR: ${err}`);
        });
    };

    po.selectListFeatureType = (listFeatureType) => {
        po.listFeatureType.element(by.cssContainingText(`option`, listFeatureType)).click().then(() => {
            log.info(`Select List feature Type : ${listFeatureType}`);
        }, (err) => {
            log.error(`Unable to select list feature type. ERROR: ${err}`);
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

module.exports = new FeatureWidgetPage();