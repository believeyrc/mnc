<div id="header-links">
  <p>  <a href="@{Homez.index()}">&{'Home'}</a>  
  #{notLogin} 
  	| <a href="@{Secure.login()}">&{'Login'}</a> 
  #{/notLogin} 
  #{isLogin} 
	  | <a href="@{Memez.index(_caller.user?.family?.code)}">&{'myHome', _caller.user?.fullname} </a> 
	  | <a href="@{Familyz.neighbours(_caller.user?.family?.code)}">&{'Neighbours'}</a> 
	  | <a href="@{Admin.index()}">&{'Administration'}</a> 
	  | <a href="@{Secure.logOut()}">&{'Logout'} </a> 
  #{/isLogin} 
  </p>
</div>
