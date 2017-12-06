const format = require('util').format;
const log = require('../util/logger');
const waitFor = require('../util/waitFor');

const DeleteConfirmationPage = function() {

    const po = this;

    po.confirmDeleteButton      =   element(by.css(`.sweet-alert .confirm`));
    po.cancelDeleteButton       =   element(by.css(`.sweet-alert .cancel`));
    po.confirmationMessage      =   element(by.css(`.sweet-alert h2`));


    po.confirmDelete = () => {
        waitFor.elementToBeClickable("Delete Confirm", po.confirmDeleteButton, 5);
        po.confirmDeleteButton.click().then(() => {
            log.info(`Confirm Delete`);
            browser.sleep(2000);
        }, (err) => {
            log.error(`Unable to confirm delete. ERROR: ${err}`);
        });
    };

    po.cancelDelete = () => {
        po.cancelDeleteButton.click().then(() => {
            log.info(`Cancel Delete`);
        }, (err) => {
            log.error(`Unable to cancel delete. ERROR: ${err}`);
        });
    };

    po.getConfirmationMessage = () => {
        po.confirmationMessage.getText().then((text) => {
            log.info(`Confirmation Message : ${text}`);
        }, (err) => {
            log.error(`Unable to get confirmation message. ERROR: ${err}`);
        });
    };

    po.isDeleteConfirmationBubble = () => {
        return po.confirmationMessage.isDisplayed().then((result) => {
            if (result) {
                log.info(`Delete Confirmation Bubble displayed`);
                return result;
            } else {
                log.info(`Delete Confirmation Bubble not displayed`);
                return result;
            }
        }, (err) => {
            log.error(`Unable to locate delete confirmation bubble. ERROR: ${err}`);
        });
    };

};

module.exports = new DeleteConfirmationPage();