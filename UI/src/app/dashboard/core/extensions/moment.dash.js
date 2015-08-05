/**
 * Moment dash
 * A fork of https://github.com/hijonathan/moment.twitter
 * Modified initialize logic to work in operative web workers
 * Formats to a dashboard-specific display with 'ago' language
 */
(function () {
    var day, formats, hour, initialize, minute, second, week;

    second = 1e3;

    minute = 6e4;

    hour = 36e5;

    day = 864e5;

    week = 6048e5;

    formats = {
        seconds: {
            short: 's',
            long: ' sec'
        },
        minutes: {
            short: 'm',
            long: ' min'
        },
        hours: {
            short: 'h',
            long: ' hr'
        },
        days: {
            short: 'd',
            long: ' day'
        }
    };

    initialize = function (moment) {
        var dashFormat;
        dashFormat = function (format, suffix, prefix) {
            var diff, num, unit, unitStr;
            diff = Math.abs(this.diff(moment()));
            unit = null;
            num = null;
            if (diff <= second) {
                unit = 'seconds';
                num = 1;
            } else if (diff < minute) {
                unit = 'seconds';
            } else if (diff < hour) {
                unit = 'minutes';
            } else if (diff < day) {
                unit = 'hours';
            } else if (format === 'short') {
                if (diff < week) {
                    unit = 'days';
                } else {
                    return this.format('MMM D');
                }
            } else {
                return this.format('MMM D');
            }
            if (!(num && unit)) {
                num = moment.duration(diff)[unit]();
            }
            unitStr = unit = formats[unit][format];
            if (format === 'long' && num > 1) {
                unitStr += 's';
            }
            return (prefix + ' ' + num + unitStr + ' ' + suffix).trim();
        };
        moment.fn.dashLong = function (suffix, prefix) {
            return dashFormat.call(this, 'long', (suffix || ''), (prefix || ''));
        };
        moment.fn.dash = moment.fn.dashShort = function (suffix, prefix) {
            return dashFormat.call(this, 'short', (suffix || ''), (prefix || ''));
        };
        return moment;
    };

    initialize(moment);

}).call(this);
