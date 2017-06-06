/**
 * Build widget configuration
 */
(function() {
	'use strict';
	
	   angular
       .module(HygieiaConfig.module).controller('CaapmConfigController',CaapmConfigController);
	
	CaapmConfigController.$inject = [ 'modalData', '$uibModalInstance','caapmData','collectorData'];
	
	function CaapmConfigController(modalData, $uibModalInstance, caapmData,collectorData) {
	
	var ctrl = this;
	var widgetConfig = modalData.widgetConfig;
	
	 // public variables
  ctrl.toolsDropdownPlaceholder = 'Loading Manage Modules...';
  ctrl.toolsDropdownDisabled = true;
  
  ctrl.submitted = false;
  ctrl.submit = submitForm; 
  ctrl.listModules = [];
  collectorData.itemsByType('CaApm').then(processResponse);
  
  function processResponse(data) {
      var worker = {
          getManageModules: getManageModules
      };

      function getManageModules(data, currentCollectorItemId, cb) {
          var manageModules = [],
              selectedIndex = null;

          for (var x = 0; x < data.length; x++) {
              var obj = data[x];
              var item = {
                  value: obj.id,
                  name: obj.options.manModuleName,
                  group: obj.options.manModuleName
              };
              manageModules.push(item);

              if (currentCollectorItemId !== null && item.value == currentCollectorItemId) {
                  selectedIndex = x;
              }
          }

          cb({
        	  manageModules: manageModules,
              selectedIndex: selectedIndex
          });
      }
      var caApmCollector = modalData.dashboard.application.components[0].collectorItems.CaApm;
      var caApmCollectorId = caApmCollector ? caApmCollector[0].id : null;
      worker.getManageModules(data, caApmCollectorId, getManageModulesCallback);
  }


  function getManageModulesCallback(data) {
      ctrl.listModules = data.manageModules;
      ctrl.oridata = data.manageModules;
          ctrl.toolsDropdownPlaceholder = 'Select a Manage Module';
          ctrl.toolsDropdownDisabled = false;

          if (data.selectedIndex !== null) {
              ctrl.collectorItemId = ctrl.listModules[data.selectedIndex];
          }
  }


  function paginationFetch($select, $event, x) {
      if (!$event) {
//          console.log("called first time")
      } else {
          $event.stopPropagation();
          $event.preventDefault();
//          console.log("called subsequent time");
          updatePaginationVariables(ctrl.oridata, x);
      }

  }


  function updatePaginationVariables(m, startIndex) {

//      console.log("Before:" + m.length);
      var y = [];

      if (m.length <= 200) {
          y = m;
          ctrl.loading = false;
      }
      else {
          for (var p = 0; p < ctrl.paginationRange; p++) {
              var value = {
                  value: m[p].value,
                  name: m[p].name
              }

              y.push(value);
              m.shift();
          }
      }

      //console.log("Y is :" + JSON.stringify(y));

      $scope.$applyAsync(function () {
          ctrl.listModules = y;
      });

//      console.log("After:" + m.length);

  }
  
  function submitForm(valid,module) {
	   ctrl.submitted = true;
      if (valid) {
          var form = document.configForm;
          var postObj = {
              name: 'caapm',
              options: {
                  id: widgetConfig.options.id,
                  moduleName: module.value,
                  name : module.name
              },
              componentId: modalData.dashboard.application.components[0].id,
              collectorItemId: module.value
              
          };              
          $uibModalInstance.close(postObj);
      }
}}
})();