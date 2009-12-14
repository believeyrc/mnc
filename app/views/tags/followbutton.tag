#{isLogin}
	#{if _caller.user.family.code != _caller.family.code && !_caller.isFollowed}
		<label><a href="@{Relationz.follow(_caller.user.family.code,_caller.family.code)}" class='float-right' style='color:#F21111'>&{'follow'}</a></label>
	#{/if}
#{/isLogin}