var commApp = angular.module('commApp', ['ngRoute', 'commissioningModule']);

commApp.config(['$routeProvider',
    function($routeProvider){
        $routeProvider.when('/home', {
            templateUrl: '/assets/index.html', controller: 'HomeCtrl'
        }).otherwise({
            redirectTo: '/home'
        });
    }
]);

commApp.controller('HomeCtrl', ['$scope', '$location', '$log',
    function($scope, $location, $log){
        $log.debug("HomeCtrl loaded");
        $scope.startCommissioning = function(){
            $log.debug("chaning path to /commission");
            $location.path("/commission");
        }
    }
]);

commApp.controller('NavCtrl', ['$scope', '$location', '$log',
    function($scope, $location, $log){
        $scope.isActive = function(viewLocation){
            $log.debug("determining active for "+viewLocation);
            return viewLocation === $location.path();
        };
    }]);