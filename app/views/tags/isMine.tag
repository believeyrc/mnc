#{isLogin}
	#{if _caller.user.family.code == _caller.family.code }
		#{doBody/}
	#{/if}
#{/isLogin}