<!DOCTYPE html>
<html lang="en" >

<head>
  <meta charset="UTF-8">
  <title>POS Demo for PZ International</title>
  
  
  <link rel='stylesheet prefetch' href='http://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css'>

      <link rel="stylesheet" href="css/style.css">

  
</head>

<body>

  <html ng-app="myApp">
<body data-ng-controller="POSController">
  <div class="container">
    <div class="row">
      <div class="col-md-12">
        <div class="jumbotron">
          <h2><span class="text-warning">DEMO POS</span> <span class="text-success">- An Example of Online Point of Sale</span></h2>
          <span class="pull-right text-muted">Business Date - May 15, 2018 (Tue)</span>
          <div class="text-muted">Dear Employees, Help the customers to build their own PIZZA</div>
        </div>
      </div>
    </div>
    
    <div class="row">
      <div class="col-sm-6">
        <div class="well">
          <div class="box">
            <div class="text-info">CRUST/TOPPINGS</div>
            <hr>
            
            <button class="buttons btn btn-primary" ng-click="add(food.pizza)">Thin Crust</button>
            <button class="buttons btn btn-primary" ng-click="add(food.pizzadeep)">Deep Dish</button>
            <button class="buttons btn btn-primary" ng-click="add(food.nakedchicken)">Naked Chicken</button>
            <button class="buttons btn btn-primary" ng-click="add(food.carribjerk)">Carribbean Jerk</button>
            <button class="buttons btn btn-success" ng-click="add(food.cheesythin)">Cheesy Thin</button>
          </div>
          
          <br></br>
          <div class="box">
            <div class="text-info">BEVERAGES/ DRINKS</div>
            <hr>
            
            <button class="buttons btn btn-primary" ng-click="add(food.coldcoffee)">Cold Coffee</button>
            <button class="buttons btn btn-primary" ng-click="add(food.hotcoffee)">Hot Coffee</button>
            <button class="buttons btn btn-primary" ng-click="add(food.coke)">Coke</button>
            <button class="buttons btn btn-primary" ng-click="add(food.dietcoke)">Diet Coke</button>
            <button class="buttons btn btn-primary" ng-click="add(food.pepsi)">Pepsi</button>
          </div>
          
        </div>
      </div>
      
      <div class="col-sm-6">
        <div class="well">
          <div class="panel panel-primary">
            <div class="panel-heading">
              <h3 class="panel-title">Order Summary</h3>
            </div>
            <div class="panel-body" style="max-height:320px; overflow:auto;">
              <div class="text-warning" ng-hide="order.length">
                Noting ordered yet !
              </div>
              <ul class="list-group">
                <li class="list-group-item" ng-repeat = "item in order">
                  <div class="label label-success">${{item.item.price}}</div>
                   {{item.item.detail}}
                  
                  <button class="btn btn-danger btn-xs pull-right" ng-click="deleteItem($index)">
                    <span class="glyphicon glyphicon-trash"></span>
                  </button>
                </li>
              </ul>

            </div>
            <div class="panel-footer" ng-show="order.length">
              <div class="label label-danger">Total: ${{getSum()}}</div>
            </div>
            <div class="panel-footer" ng-show="order.length">
              <div class="text-muted">
                Please request the customers to submit the online survey !
              </div>
            </div>
            <div class="pull-right">
              <span class="btn btn-default" ng-click="clearOrder()" ng-disabled="!order.length">Clear</span>
              <span class="btn btn-danger" ng-click="checkout()" ng-disabled="!order.length">Checkout</span>
            </div>
            
          </div>
        </div>
      </div>
    </div>
  </div>

</body>
</html>
  <script src='http://ajax.googleapis.com/ajax/libs/angularjs/1.3.2/angular.min.js'></script>
<script src='http://code.jquery.com/jquery-2.0.3.min.js'></script>
<script src='http://netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js'></script>
<script src='http://code.angularjs.org/1.2.13/angular.js'></script>
<script src='http://code.angularjs.org/1.2.13/angular-animate.js'></script>

  

    <script  src="js/index.js"></script>




</body>

</html>
