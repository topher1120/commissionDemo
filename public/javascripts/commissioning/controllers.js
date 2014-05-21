var commControllers = angular.module('commissioningControllers', []);

commControllers.controller('CommIndexCtrl', ['$scope', '$log',
    function($scope, $log){
        $log.debug("CommIndexCtrl loaded");
    }
]);