	// create the module and name it marketplace
	var marketplace = angular.module('marketplace', ['ngRoute']);

	// configure our routes
	marketplace.config(function($routeProvider) {
		$routeProvider

			// route for the home page
			.when('/', {
				templateUrl : 'pages/front.html',
				controller  : 'frontController'
			})

			// route for the about page
			.when('/agents', {
				templateUrl : 'pages/agents.html',
				controller  : 'agentsController'
			})

			// route for the contact page
			.when('/log', {
				templateUrl : 'pages/log.html',
				controller  : 'logController'
			});
	});

    marketplace.factory('communications', function($http, $interval){
        let object = {
            types: [{name:"All", filter:"", count:0}],
            products: [],
            agents: []
        };

        object.updateMoney = (agentName, amount) => {
            $http({
                method: 'GET',
                url: '/api/update-money',
                params: {agent: agentName, amount : amount}
            }).then((response) => {
                // Ignore the success
            }, (response) => {
                // Ignore the error
            });
        };

        object.updateAgents = () => {
            $http({
                method: 'GET',
                url: '/api/agents'
            }).then(function successCallback(response) {
                for (let ag of response.data) {
                    let agent = object.agents.find((a)=> a.name == ag.name);
                    if (agent === undefined) {
                        object.agents.push(ag);
                    } else {
                        // Just update the log
                        agent.log = ag.log;
                    }
                }
            }, function errorCallback(response) {
                // Ignore the error
            });
        };

        object.updateProducts = () => {
            $http({
                method: 'GET',
                url: '/api/onsale'
            }).then(function successCallback(response) {
                // Look for each product
                for (let type of object.types) {
                    type.count = 0;
                }
                let toadd = [];
                for (let product of response.data) {
                    // Update counters for types
                    object.types[0].count++;
                    let type = object.types.find((t) => t.filter === product.name
                )
                    ;
                    if (type === undefined) {
                        object.types.push({name: product.name, filter: product.name, count: 1});
                    } else {
                        type.count++;
                    }

                    let current = object.products.findIndex((p) => p.id == product.id
                )
                    ;
                    if (current === -1) {
                        toadd.push(product);
                    } else if (product.last_modified > object.products[current].last_modified) {
                        object.products[current] = product;
                    }
                }
                object.products.push(...toadd
                )
                ;
            }, function errorCallback(response) {
                // Ignore the error
            });
        };

        $interval(() => {
            object.updateProducts();
            object.updateAgents();
        }, 3000); // Each second

        object.updateAgents();
        object.updateProducts();

        return object;
    });

	marketplace.controller('frontController', ['$scope', 'communications', function($scope, communications) {

        $scope.products = communications.products;
        $scope.filter = "";
        $scope.types = communications.types;

        $scope.setProductFilter = function(filter) {
          $scope.filter = filter;
        };


	}]);

	marketplace.controller('agentsController', ['$scope', 'communications', function($scope, communications) {
		$scope.selected = null

		$scope.moneyDiff = null;

		$scope.selectAgent = function(agent) {
		  $scope.selected = agent;
        };
		
		$scope.updateMoney = function (agentName, amount) {
          amount = parseFloat(amount);
          if (isNaN(amount)) {
              alert("Invalid amount: "+amount);
          } else {
              communications.updateMoney(agentName, amount);
          }

          $scope.moneyDiff=null;
        };
		$scope.agents = communications.agents;
	}]);

	marketplace.controller('logController', ['$scope', '$http', function($scope, $http) {
		$scope.events = [];
        $http({
            method: 'GET',
            url: '/api/events'
        }).then(function successCallback(response) {
        	console.log("RESPONSE", response);
			$scope.events = response.data;
        }, function errorCallback(response) {
            // Ignore the error
        });
	}]);