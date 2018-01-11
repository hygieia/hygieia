
class Timeouts {

    constructor() {
        this.defaultTimeOut = 30000;
        this.shortTimeOut = 5000;
        this.mediumTimeOut = 30000;
        this.longTimeOut = 120000;

        this.defaultTimeOutInSeconds = 3;
        this.shortTimeOutInSeconds = 5;
        this.mediumTimeOutInSeconds = 30;
        this.longTimeOutInSeconds = 120;
    }

}

module.exports = new Timeouts();