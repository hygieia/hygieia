/**
 * Checks widget values to see if they contain values
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .filter('checkEmpty', checkEmptyFilter);

    function checkEmptyFilter() {
        return function(input) {
            return input ? moment(input).dash('ago') : '';
        };
    }
})();