<div id="header-links">
  <p>  <a href="@{Homez.index()}">&{'Home'}</a>  
  #{notLogin} 
  	| <a href="@{Secure.login()}">&{'Login'}</a> 
  #{/notLogin} 
  #{isLogin} 
	  | <a href="@{Memez.index(_caller.user?.family?.code)}">&{'myHome', _caller.user?.fullname} </a> 
	  | <a href="@{Familyz.neighbours(_caller.user?.family?.code)}">&{'Neighbours'}</a> 
  #{secure.check "ROLE_ADMIN"}
	  | <a href="@{Admin.index()}">&{'Administration'}</a> 
  #{/secure.check}
	  | <a href="@{Secure.logOut()}">&{'Logout'} </a> 
  #{/isLogin} 
  </p>
</div>
