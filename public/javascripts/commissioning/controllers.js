var commModule = angular.module('commissioningModule', ['ngRoute', 'ui.bootstrap']);

commModule.config(['$routeProvider',
    function($routeProvider){
        $routeProvider.when('/commission', {
            templateUrl: '/assets/commissioning/index.html', controller: 'CommIndexCtrl'
        }).when('/commission/checkConfig', {
            templateUrl: '/assets/commissioning/checkConfig.html', controller: 'CheckConfigCtrl'
        }).when('/commission/performCommission', {
            templateUrl: '/assets/commissioning/performCommission.html', controller: 'PerformCommissionCtrl'
        }).when('/commission/selectDevices', {
            templateUrl: '/assets/commissioning/selectDevices.html', controller: 'SelectDevicesCtrl'
        });
    }
]);

commModule.controller('CommIndexCtrl', ['$scope', 'CommissionService', '$location', '$log',
    function($scope, CommissionService, $location, $log){
        'use strict';

        $log.debug("CommIndexCtrl loaded");

        CommissionService.gatewayId = '';

        $scope.submitGateway = function(gateway){
            $log.info("gateway: "+gateway);
            CommissionService.gatewayId = gateway;
            $log.info("GatewayService id: "+CommissionService.gatewayId);
            $location.path("/commission/checkConfig");
        }

    }
]);

commModule.controller('CheckConfigCtrl', ['$scope', 'CommissionService', '$location', '$modal', '$log',
    function($scope, CommissionService, $location, $modal, $log){
        'use strict';

        $scope.gatewayId = CommissionService.gatewayId;
        $scope.configurations = [];
        $scope.progress = 1;
        $scope.progressMax = 10;
        $scope.progressLabel = "Checking Gateway";
        $scope.gwTimezone = 'America/Denver';

        var progressBar = $modal.open({
            templateUrl: '/assets/commissioning/progressDialog.html',
            backdrop: 'static',
            scope: $scope
        });
        var ws = new WebSocket(myJsRoutes.controllers.Commissioning.checkConfig(CommissionService.gatewayId).webSocketURL());

        ws.onmessage = function(message){
            $scope.$apply(function () {

                var data = JSON.parse(message.data);
                $log.debug("received message, event type: "+data.eventType);
                if(data.eventType === "Notification"){
                    $scope.progressLabel = data.description;
                } else{
                    //config event
                    $scope.configurations.push(data);
                }
                $scope.progress++;
            });
        };

        ws.onclose = function(){
            $log.debug("websocket closed");
            progressBar.close();
        };

        $scope.cancel = function(){
            $log.debug("cancel called");
            $location.path("/commission");
            $location.replace();
        };

        $scope.next = function(){
            $log.debug("next called");
            $location.path("/commission/performCommission");
            $location.replace();
        }
    }
]);

commModule.controller('PerformCommissionCtrl', ['$scope', 'CommissionService', '$location', '$modal', '$log',
    function($scope, CommissionService, $location, $modal, $log){
        'use strict';

        $scope.gatewayId = CommissionService.gatewayId;
        $scope.configurations = [];
        $scope.progress = 1;
        $scope.progressMax = 10;
        $scope.progressLabel = "Commissioning Gateway";
        var progressBar = $modal.open({
            templateUrl: '/assets/commissioning/progressDialog.html',
            backdrop: 'static',
            scope: $scope
        });

        var ws = new WebSocket(myJsRoutes.controllers.Commissioning.performCommission(CommissionService.gatewayId).webSocketURL());

        ws.onmessage = function(message){
            $scope.$apply(function () {

                var data = JSON.parse(message.data);
                $log.debug("received message, event type: "+data.eventType);
                if(data.eventType === "Notification"){
                    $scope.progressLabel = data.description;
                } else{
                    //config event
                    $scope.configurations.push(data);
                }
                $scope.progress++;
            });
        };

        ws.onclose = function(){
            $log.debug("websocket closed");
            progressBar.close();
        };

        $scope.next = function(){
            $log.debug("next called");
            $location.path("/commission");
            $location.replace();
        };
    }
]);

commModule.controller('SelectDevicesCtrl', ['$scope', 'CommissionService', '$log',
    function($scope, CommissionService, $log){
        'use strict';
        $log.debug("entered SelectDevicesCtrl");
    }

]);

commModule.service('CommissionService', function(){
        this.gatewayId = '';
    }
);