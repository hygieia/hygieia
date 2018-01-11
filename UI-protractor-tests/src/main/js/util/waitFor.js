
const timeout = require('../util/timeouts');
const log = require('../util/logger');
const EC = protractor.ExpectedConditions;
const maxRetries = 5;

class WaitFor {

    elementToBeClickable(elementName, element, retryCount) {
        if (retryCount === null || isNaN(retryCount)) {
            retryCount = 0;
        }

        browser.wait(EC.elementToBeClickable(element), timeout.defaultTimeOut).then(() => {}, (err) => {
            log.info(`Element '${elementName}' is still not visible after ${timeout.defaultTimeOut / 1000} seconds, checking again`);
            if (retryCount > maxRetries) {
                log.info(err);
                throw err;
            } else {
                this.elementToBeClickable(elementName, element, ++retryCount);
            }
        });
    }

    elementToBeVisible(elementName, element, retryCount) {
        if (retryCount === null || isNaN(retryCount)) {
            retryCount = 0;
        }

        browser.wait(EC.visibilityOf(element), timeout.defaultTimeOut).then(() => {}, (err) => {
            log.info(`Element '${elementName}' is still not visible after ${timeout.defaultTimeOut / 1000} seconds, checking again`);
            if (retryCount > maxRetries) {
                log.info(err);
                throw err;
            } else {
                this.elementToBeVisible(elementName, element, ++retryCount);
            }
        });
    }

    elementToBeInvisible(elementName, element, retryCount) {
        if (retryCount === null || isNaN(retryCount)) {
            retryCount = 0;
        }

        browser.wait(EC.invisibilityOf(element), timeout.defaultTimeOut).then(() => {}, (err) => {
            log.info(`Element '${elementName}' is still visible after ${timeout.defaultTimeOut / 1000} seconds, checking again`);
            if (retryCount > maxRetries) {
                log.info(err);
                throw err;
            } else {
                this.elementToBeInvisible(elementName, element, ++retryCount);
            }
        });
    }

    titleToBe(title, retryCount) {
        if (retryCount === null || isNaN(retryCount)) {
            retryCount = 0;
        }

        browser.wait(EC.titleContains(title), timeout.defaultTimeOut).then(() => {}, (err) => {
            log.info(`Page title is still not '${title}' after ${timeout.defaultTimeOut / 1000} seconds, checking again`);
            if (retryCount > maxRetries) {
                log.info(err);
                throw err;
            } else {
                this.titleToBe(title, ++retryCount);
            }
        });
    }

    elementTextToBe(elementName, element, text, retryCount) {
        if (retryCount === null || isNaN(retryCount)) {
            retryCount = 0;
        }

        browser.wait(EC.textToBePresentInElement(element, text, timeout.defaultTimeOut)).then(() => {}, (err) => {
            log.info(`Element '${elementName}' still not contain text '${text}' after ${timeout.defaultTimeOut / 1000} seconds, checking again`);
            if (retryCount > maxRetries) {
                log.info(err);
                throw err;
            } else {
                this.elementTextToBe(elementName, element, text, ++retryCount);
            }
        });
    }

}

module.exports = new WaitFor();