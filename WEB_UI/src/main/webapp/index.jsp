<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Welcome To Rizpa Stock Exchange</title>

  <script src="/common/jquery-2.0.3.min.js"></script>
  <!--<script src="/pages/login/login.js"></script>-->
  <link rel="stylesheet" type="text/css" href="pages/login/login.css">
</head>

<body id="login-page">
<div id="login-form" class="container">
  <h1 id="main-header" >Welcome To Rizpa Stock Exchange</h1>
  <br/>
  <h2>Please enter your name: </h2>
  <form id="loginForm" method="GET" action="/control/login">
    <input type="text" name="username" class=""/>
    <input type="checkbox" name="is_admin" value="false"/>
    <input type="submit" value="Login"/>
  </form>
  <div id="error-placeholder" class="alert-danger" role="alert"></div>
</div>
</body>
</html>