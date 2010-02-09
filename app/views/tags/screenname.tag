#{ifnot _href}
	<a href="@{Photov.home(_arg?.fullname)}">${_arg?.screenname ? _arg?.screenname : _arg?.fullname}</a>
#{/ifnot}
#{if _href}
	<a href="${_href}">${_arg?.screenname ? _arg?.screenname : _arg?.fullname}</a>
#{/if}