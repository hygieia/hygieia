/**
 * Gets gates related data
 */
(function() {
  'use strict';

  angular
    .module(HygieiaConfig.module + '.core')
    .factory('cicdGatesData', cicdGatesData);

  var injector;

  //finds the required arguments and assign value to it
  var prepArguments = function(args, name, dashboardId, collectorItemId, componentId) {
    var arr = [];
    //in the sequetnce in which it was defined..
    for (var i = 0; i < args.length; i++) {
      if (args[i].value) {
        var val = args[i].value;
        if (typeof val == 'object') {
          val = JSON.stringify(val);
          val = val.replace("$dashboardid", dashboardId);
          val = val.replace("$collectoritemid", collectorItemId);
          val = val.replace("$name", name);
          val = val.replace("$componentid", componentId);
          val = JSON.parse(val);
        }
        arr.push(val);
      } else {
        //handle special params.
        if (args[i].name.toLowerCase() == '$dashboardid') {
          arr.push(dashboardId);
        } else if (args[i].name.toLowerCase() == "$collectoritemid") {
          arr.push(collectorItemId);
        } else if (args[i].name.toLowerCase() == "name") {
          arr.push(name);
        } else {
          console.log("invalid paramter..where do I get it?");
        }
      }
    }
    if (arr.length == 0) {
      return null;
    }
    return arr;
  };


  //this returns object which is used to run jsonlogic.apply
  var prepareEvalObject = function(res, data, name, dashboardId, collectorItemId, componentId) {
    //extract the object which we will evaluate the rule with.
    var loc = data.source.result.location;
    var parts = loc.split(".");
    var evalWith = res; //this will be used to evaluate json rule.
    for (var i = 0; i < parts.length; i++) {
      var prop = parts[i].trim();
      if (prop) {
        evalWith = evalWith[prop];
        if (!evalWith) {
          return null;
        }
      }
    }

    //replace any $ conditions with values.
    var rule = data.source.result.rule;
    var lengthBasedRule = false;
    if (rule) {
      //relapce all $ items with values from context/paramter.
      var str = JSON.stringify(rule);
      str = str.replace("$dashboardid", dashboardId);
      str = str.replace("$collectoritemid", collectorItemId);
      str = str.replace("$componentid", componentId);
      str = str.replace("$name", name);
      lengthBasedRule = str.indexOf("$length") > 0;
      rule = JSON.parse(str);
    }

    //if we are working in an array
    var extracted = {};

    if (rule && evalWith.length) {
      for (var j = 0; j < rule.length; j++) {
        for (var i = 0; i < evalWith.length; i++) {
          var match = false;
          if (lengthBasedRule) {
            var index = eval(rule[j].replace("$length", evalWith.length));
            if (i == index) {
              match = true;
            }
          } else {
            match = jsonLogic.apply(rule[j], evalWith[i]);
          }
          if (match) {
            if (!data.source.result.find) {
              evalWith = evalWith[i];
              break;
            } else {
              var name = null;
              for (var x in rule[j]) {
                name = rule[j][x][1];
                break;
              }
              extracted[name] = evalWith[i][data.source.result.find]
            }
          }
        }
      }
    }


    if (data.source.result.find) {
      evalWith = extracted;
    }
    if (Array.isArray(evalWith) && evalWith.length == 0) {
      return null;
    }
    return evalWith;
  };



  //this function is responsible for making the factory call
  var factoryCaller = function(data, $q, name, dashboardId, collectorItemId, componentId) {
    var defer = $q.defer();
    var factory = null;
    if (data.source.api != "NA") {
      factory = injector.get(data.source.api); //synamically get the factory from injector
    }
    if (!factory) {
      if (data.source.api == "NA") {
        data.value = "NA";
      } else {
        data.value = "fail";
      }
      defer.resolve(data);
    } else {
      var fun = factory[data.source.method]; //method of the factory to be calculateTechnicalDebt
      var argsForFactoryMethod = prepArguments(data.source.args, name, dashboardId, collectorItemId, componentId); //arguments to be passed in the factory call
      fun.apply(this, argsForFactoryMethod).
      then(function(res) {
        if (!res) {
          data.value = "fail";
          return defer.resolve(data);
        }
        //prepapre the object whcih we need for json.appy an rule evaluation.
        var evalWith = prepareEvalObject(res, data, name, dashboardId, collectorItemId, componentId);
        // var d = res[data.source.result.location]
        if (evalWith) {
          if (typeof evalWith != 'object') {
            evalWith = {
              compare: evalWith
            };
          }
          var result = jsonLogic.apply(data.rules, evalWith);
          data.value = result ? "pass" : "fail"; //evaludat ethe rule for gate,
        } else {
          data.value = "fail";
        }

        defer.resolve(data);
      });
    }

    return defer.promise;
  };

  //evalvates all gates. and after evaluation it will return result
  var fillDetails = function(data, $q, name, dashboardId, collectorItemId, componentId) {
    var defer = $q.defer();
    var allTasks = [];

    _(data).forEach(function (gate) {
      allTasks.push(factoryCaller(gate, $q, name, dashboardId, collectorItemId, componentId));
    });
    $q.all(allTasks).then(function(res) {
      defer.resolve(res);
    });
    return defer.promise;
  };


  function profilesData($http) {
    var testProfilesRoute = 'test-data/profiles.json';
    var profilesRoute = '/api/maturityModel/profiles';
    return $http.get(HygieiaConfig.local ? testProfilesRoute : profilesRoute).then(function(response) {
      return response.data;
    });
  }

  function cicdGatesData($http, $q, $injector) {

    injector = $injector;

    function details(name, dashboardId, collectorItemId, componentId) {

      return profilesData($http).then(function(res) {
        var testDetailRoute = 'test-data/cicd-gates.json';
        var detailRoute = '/api/maturityModel/profile';
        var profileId = res[0].profile;
        return $http.get(HygieiaConfig.local ? testDetailRoute : detailRoute + '/' + profileId)
          .then(function(response) {
            var data = response.data.rules;
            var jsonObj = JSON.parse(data);
            return fillDetails(jsonObj, $q, name, dashboardId, collectorItemId, componentId).then(function(d) {
              return d;
            });
          });

      });
    }


    return {
      details: details
    };


  }
})();
