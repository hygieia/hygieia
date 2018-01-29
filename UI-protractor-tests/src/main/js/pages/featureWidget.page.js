const format = require('util').format;
const log = require('../util/logger');
const waitFor = require('../util/waitFor');

const FeatureWidgetPage = function() {

    const po = this;

    po.featureDataSourceDropDown    =   element(by.name(`collectorId`));
    po.projectName                  =   element(by.name(`projectName`));
    po.projectNameDropDown          =   element(by.css(`[input="projectName"] ul`));
    po.teamName                     =   element(by.name(`teamName`));
    po.teamNameDropDown             =   element(by.css(`[input="teamName"] ul`));
    po.estimateMetricType           =   element(by.name(`estimateMetricType`));
    po.sprintType                   =   element(by.name(`sprintType`));
    po.listFeatureType              =   element(by.name(`listType`));
    po.saveButton                   =   element(by.cssContainingText(`.btn.btn-primary.btn-wide`, "Save"));
    po.kanbanProjectNameLabel       =   element(by.cssContainingText(`#kanban-widget-view .text-standard-sm`, "Project:"));
    po.projectNameInWidget          =   po.kanbanProjectNameLabel.element(by.tagName(`em`));

    po.selectAgileContentToolType = (datasource) => {
        po.featureDataSourceDropDown.element(by.cssContainingText(`option`, datasource)).click().then(() => {
            log.info(`Select Agile Content Tool Type : ${datasource}`);
        }, (err) => {
            log.error(`Unable to select agile content tool type. ERROR: ${err}`);
        });
    };

    po.setProjectName = (projectName) => {
        po.projectName.sendKeys(projectName).then(() => {
            po.projectNameDropDown.element(by.cssContainingText(`li a`, projectName)).click().then(() => {
                log.info(`Select Project Name : ${projectName}`);
            }, (err) => {
                log.error(`Unable to select project name. ERROR: ${err}`);
            });
        }, (err) => {
            log.error(`Unable to set project name. ERROR: ${err}`);
        });
    };

    po.setTeamName = (teamName) => {
        po.teamName.sendKeys(teamName).then(() => {
            po.teamNameDropDown.element(by.cssContainingText(`li a`, teamName)).click().then(() => {
                log.info(`Select Team Name : ${teamName}`);
            }, (err) => {
                log.error(`Unable to select team name. ERROR: ${err}`);
            });
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

    po.getProjectName = () => {
        waitFor.elementToBeVisible("Project Name", po.projectNameInWidget, 5);
        return po.projectNameInWidget.getText().then((text) => {
            log.info(`Project Name : ${text}`);
            return text;
        }, (err) => {
            log.error(`Unable to get project name. ERROR: ${err}`);
        });
    }

};

module.exports = new FeatureWidgetPage();