
var app = angular.module('agm', ['ionic']);

app.controller('agmCtrl', function($scope, $http, $httpParamSerializer){

  var agmUrl = 'http://localhost:8100/agm/';
  // var agmUrl = 'https://<the-server-you-are-working-on>/agm/';
  $scope.users = [];
  var data = {
    
      'grant_type':'client_credentials',
      'client_id':'',
      'client_secret':''
    
  };
  var headers = {
    'Content-Type': 'application/x-www-form-urlencoded'
  };

  $http.post(agmUrl + 'oauth/token',
    $httpParamSerializer(data),{headers:headers}).success(function(result){
    var token = result['access_token'];
 
        headers = {
          'Authorization': 'bearer ' + token,
          'Content-Type': 'application/json'
        };

        $http.get(agmUrl + 'api/workspaces/1000/team_members',{headers:headers}).success(function(result){
          console.log(result);
          angular.forEach(result.data, function(user){
            $scope.users.push({
              name: user['member_name'],
              id: user.id
            });
          });
        });
  });

});

app.run(function($ionicPlatform) {
  $ionicPlatform.ready(function() {
    // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
    // for form inputs)
  if(window.cordova && window.cordova.plugins.Keyboard) {
    cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
  }
  if(window.StatusBar) {
    StatusBar.styleDefault();
  }
});
});
