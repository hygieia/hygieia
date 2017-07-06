(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('pipelineConfigController', pipelineConfigController);

    pipelineConfigController.$inject = ['modalData', 'deployData', '$uibModalInstance', '$q'];
    function pipelineConfigController(modalData, deployData, $uibModalInstance, $q) {
        /*jshint validthis:true */
        var ctrl = this;

        // make sure mappings property is available
        ctrl.environmentsDropdownDisabled = true;
        ctrl.environmentMappings = [ ];
        ctrl.saveDisabled = false;
        ctrl.saveDisabledDropDown = false;
        ctrl.radioValue =[];

        ctrl.save = save;
        ctrl.deleteMapping = deleteMapping;
        ctrl.addMapping = addMapping;
        ctrl.validateStage = validateStage;
        ctrl.validateDropDown = validateDropDown;

        $q.all([deployData.details(modalData.dashboard.application.components[0].id)]).then(processResponse);

        function processResponse(dataA) {

            var data = dataA[0];

            for(var x in modalData.widgetConfig.options.mappings) {
                var envName = modalData.widgetConfig.options.mappings[x];
                ctrl.environmentMappings.push({key: x , value: envName});
            }

            if(modalData.widgetConfig.options.mappings) {
                _(ctrl.environmentMappings).forEach(function(env) {
                    if(modalData.widgetConfig.options.mappings[env.key]) {
                        env.value = modalData.widgetConfig.options.mappings[env.key];
                    }
                });
            }

            ctrl.environments = _(data.result).map(function (env) {
                return {
                    name: env.name,
                    value: env.name.toLowerCase()
                };
            }).value();

            ctrl.mappings = {};
            ctrl.order = {};

            for(var x in modalData.widgetConfig.options.mappings) {
                var envName = modalData.widgetConfig.options.mappings[x];
                if(_(ctrl.environments).filter({'value':envName}).value().length) {
                    ctrl.mappings[x] = envName;
                }
            }
            ctrl.radioValue = modalData.widgetConfig.options.prod;
            ctrl.environmentsDropdownDisabled = false;
        }

        function save(form) {
            var count = 0;
            if(form.$valid){
                modalData.widgetConfig.name = 'pipeline';
                ctrl.mappings = editMappings(ctrl.radioValue);
                modalData.widgetConfig.options.prod = ctrl.radioValue;
                modalData.widgetConfig.options.mappings = ctrl.mappings;
                for(var env in ctrl.mappings){
                    ctrl.order[count++] = env;
                }
                modalData.widgetConfig.options.order = ctrl.order;
                var postObj = angular.copy(modalData.widgetConfig);
                $uibModalInstance.close(postObj);
            }
        }

        function editMappings(radio){
            var mappingsTemp ={};
            _(ctrl.environmentMappings).forEach(function (env) {
                if( env.key != radio){
                    mappingsTemp[env.key] =ctrl.mappings[env.key];
                }
            });
            mappingsTemp[radio] = ctrl.mappings[radio];
            return mappingsTemp;
        }

        function addMapping() {
            var newItemNo = ctrl.environmentMappings.length + 1;
            ctrl.radioValue ='';
            ctrl.environmentMappings.push({key: 'Env' + newItemNo, value: null});
        }
      
        function deleteMapping(item) {
            var index = ctrl.environmentMappings.indexOf(item);
            ctrl.environmentMappings.splice(index, 1);
            if (item.key == modalData.widgetConfig.options.prod) {
                ctrl.radioValue = '';
            }
        }

        function validateStage() {
            var sortedMap = ctrl.environmentMappings.concat().sort(function (a, b) {
                if (a.key > b.key) return 1;
                if (a.key < b.key) return -1;
                return 0;
            });

            var map = find_duplicates(sortedMap, false);

            _(sortedMap).forEach(function (item) {
                item.isDuplicate = false;
            });

            _(map).forEach(function (env) {
                for (var i = 0; i < env.length; i++) {
                    sortedMap[env[i]].isDuplicate = true;
                }
            });
            for(var i=0;i<sortedMap.length;i++){
                ctrl.saveDisabled = sortedMap[i].isDuplicate;
                if(ctrl.saveDisabled) break;
            }

        }

        function validateDropDown() {
            var sortedMap;
            sortedMap=ctrl.environmentMappings.concat().sort(function (a, b) {
                if (a.key > b.key) return 1;
                if (a.key < b.key) return -1;
                return 0;
            });

            _(sortedMap).forEach(function (item) {
                item.isDuplicateDropDown = false;
            });

            var map = find_duplicates(sortedMap, true);
            _(map).forEach(function (env) {
                for (var i = 0; i < env.length; i++) {
                    sortedMap[env[i]].isDuplicateDropDown = true;
                }
            });
            for(var i=0;i<sortedMap.length;i++){
                ctrl.saveDisabledDropDown = sortedMap[i].isDuplicateDropDown;
                if(ctrl.saveDisabledDropDown ) break;
            }

        }

        function find_duplicates(sorted, value) {
            var map = {};
            var obj;
            for (var i=0;i<sorted.length;i++) {
                if(value){
                    obj=ctrl.mappings[sorted[i].key];
                }else{
                    obj=sorted[i].key.toUpperCase();
                }
                if(!map[obj]){
                    map[obj]=[i];
                }else{
                    map[obj].push(i);
                }
            }
            for(var obj in map){
                if(map[obj].length === 1){
                    delete map[obj];
                }
            }
            return map;
        }

    }
})();