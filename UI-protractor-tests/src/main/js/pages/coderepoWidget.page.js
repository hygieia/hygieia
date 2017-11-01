const format = require('util').format;
const log = require('../util/logger');
const waitFor = require('../util/waitFor');

const CodeRepoWidgetPage = function() {

    const po = this;

    po.repoTypeDropDown     =   element(by.name(`repoOption`));
    po.repoURL              =   element(by.name(`repoUrl`));
    po.branch               =   element(by.name(`gitBranch`));
    po.issuesLabelInWidget  =   element(by.cssContainingText(`[name="repo"] .widget-heading`, "Issues,"));
    po.saveButton           =   element(by.css(`[name="configForm"] .btn`));

    po.selectRepoType = (repoType) => {
        po.repoTypeDropDown.element(by.cssContainingText(`option`, repoType)).click().then(() => {
            log.info(`Select Repo Type : ${repoType}`);
        }, (err) => {
            log.error(`Unable to select repo type. ERROR: ${err}`);
        });
    };

    po.setRepoURL = (repoURL) => {
        po.repoURL.sendKeys(repoURL).then(() => {
            log.info(`Set Repo URL : ${repoURL}`);
        }, (err) => {
            log.error(`Unable to set repo url. ERROR: ${err}`);
        });
    };

    po.setBranch = (branch) => {
        po.branch.sendKeys(branch).then(() => {
            log.info(`Set Branch : ${branch}`);
        }, (err) => {
            log.error(`Unable to set branch. ERROR: ${err}`);
        });
    };

    po.getIssuesLabel = () => {
        waitFor.elementToBeVisible("Issues Label", po.issuesLabelInWidget, 5);
        return po.issuesLabelInWidget.getText().then((text) => {
            log.info(`Label Text : ${text}`);
            return text;
        }, (err) => {
            log.error(`Unable to get label text. ERROR : ${err}`);
        })
    }

    po.clickSaveButton = () => {
        po.saveButton.click().then(() => {
            log.info(`Click Save Button`);
        }, (err) => {
            log.error(`Unable to click save button. ERROR: ${err}`);
        });
    };

};

module.exports = new CodeRepoWidgetPage();