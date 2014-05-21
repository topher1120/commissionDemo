var commApp = angular.module('commApp', ['ngRoute', 'commissioningControllers']);

commApp.config(['$routeProvider',
    function($routeProvider){
        $routeProvider.when('/home', {
            templateUrl: '/assets/index.html', controller: 'HomeCtrl'
        }).when('/commission', {
            templateUrl: '/assets/commissioning/index.html', controller: 'CommIndexCtrl'
        }).otherwise({
            redirectTo: '/home'
        });
    }
]);

commApp.controller('HomeCtrl', ['$log',
    function($log){
        $log.debug("HomeCtrl loaded");
    }
]);

commApp.controller('NavCtrl', ['$scope', '$location', '$log',
    function($scope, $location, $log){
        $scope.isActive = function(viewLocation){
            $log.debug("determining active for "+viewLocation);
            return viewLocation === $location.path();
        };
    }]);