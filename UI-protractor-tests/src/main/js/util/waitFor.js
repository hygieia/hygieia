/**
 * Created by emb235 on 7/29/17.
 */

"use strict";

const EC = protractor.ExpectedConditions;
const maxRetries = 5;

class WaitFor {

    elementToBeVisible(elementName, element, retryCount) {
        if (retryCount === null || isNaN(retryCount)) {
            retryCount = 0;
        }

        browser.wait(EC.visibilityOf(element), mGlobal.timeout.defaultTimeOut).then(() => {}, (err) => {
            log.info(`Element '${elementName}' is still not visible after ${mGlobal.timeout.defaultTimeOut / 1000} seconds, checking again`);
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

        browser.wait(EC.invisibilityOf(element), mGlobal.timeout.defaultTimeOut).then(() => {}, (err) => {
            log.info(`Element '${elementName}' is still visible after ${mGlobal.timeout.defaultTimeOut / 1000} seconds, checking again`);
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

        browser.wait(EC.titleContains(title), mGlobal.timeout.defaultTimeOut).then(() => {}, (err) => {
            log.info(`Page title is still not '${title}' after ${mGlobal.timeout.defaultTimeOut / 1000} seconds, checking again`);
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

        browser.wait(EC.textToBePresentInElement(element, text, mGlobal.timeout.defaultTimeOut)).then(() => {}, (err) => {
            log.info(`Element '${elementName}' still not contain text '${text}' after ${mGlobal.timeout.defaultTimeOut / 1000} seconds, checking again`);
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