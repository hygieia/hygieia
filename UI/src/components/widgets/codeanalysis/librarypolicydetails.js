(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('LibraryPolicyDetailsController', LibraryPolicyDetailsController);

    LibraryPolicyDetailsController.$inject = ['$scope', '$uibModalInstance', 'libraryPolicyResult'];
    function LibraryPolicyDetailsController($scope, $uibModalInstance, libraryPolicyResult) {
        /*jshint validthis:true */
        var ctrl = this;
        ctrl.type = libraryPolicyResult.type;

        ctrl.libraryPolicyResult = libraryPolicyResult.data;
        ctrl.close = close;

        function close() {
            $uibModalInstance.dismiss('close');
        }


        $scope.getDashStatus = function getDashStatus(level) {
            switch (level.toLowerCase()) {
                case 'critical':
                    return 'critical';

                case 'high':
                    return 'alert';

                case 'medium':
                    return 'warning';

                case 'low' :
                    return 'ignore';

                default:
                    return 'ok';
            }
        };

        ctrl.getLevelCount = function getLevelCount(level) {
            var threats;
            if (!ctrl.libraryPolicyResult || !ctrl.libraryPolicyResult.threats) return (0);
            if (ctrl.type.toLowerCase() === 'license') {
                threats = ctrl.libraryPolicyResult.threats.License;
            } else {
                threats = ctrl.libraryPolicyResult.threats.Security;
            }
            for (var i = 0; i < threats.length; ++i) {
                if (threats[i].level.toLowerCase() === level.toLowerCase()) {
                    return threats[i].count;
                }
            }
            return (0);
        };

        ctrl.getDetails = function getDetails(level) {
            var threats;
            if (!ctrl.libraryPolicyResult || !ctrl.libraryPolicyResult.threats) return ([]);
            if (ctrl.type.toLowerCase() === 'license') {
                threats = ctrl.libraryPolicyResult.threats.License;
            } else {
                threats = ctrl.libraryPolicyResult.threats.Security;
            }
            for (var i = 0; i < threats.length; ++i) {
                if (threats[i].level.toLowerCase() === level.toLowerCase()) {
                    return threats[i].components;
                }
            }
            return ([]);
        }
    }


})();