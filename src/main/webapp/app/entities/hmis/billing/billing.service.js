(function() {
    'use strict';
    angular
        .module('hmisApp')
        .factory('Billing', Billing);

    Billing.$inject = ['$resource', 'DateUtils'];

    function Billing ($resource, DateUtils) {
        var resourceUrl =  'api/billings/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.createdate = DateUtils.convertLocalDateFromServer(data.createdate);
                        data.updateDate = DateUtils.convertLocalDateFromServer(data.updateDate);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.createdate = DateUtils.convertLocalDateToServer(copy.createdate);
                    copy.updateDate = DateUtils.convertLocalDateToServer(copy.updateDate);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.createdate = DateUtils.convertLocalDateToServer(copy.createdate);
                    copy.updateDate = DateUtils.convertLocalDateToServer(copy.updateDate);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
