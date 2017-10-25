const format = require('util').format;
const log = require('../util/logger');

const CodeRepoWidgetPage = function() {

    const po = this;

    po.repoTypeDropDown     =   element(by.name(`repoOption`));
    po.repoURL              =   element(by.name(`repoUrl`));
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

    po.clickSaveButton = () => {
        po.saveButton.click().then(() => {
            log.info(`Click Save Button`);
        }, (err) => {
            log.error(`Unable to click save button. ERROR: ${err}`);
        });
    };

};

module.exports = new CodeRepoWidgetPage();