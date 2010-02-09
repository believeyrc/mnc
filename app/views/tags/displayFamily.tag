
<li>
	<a href="/family/${_family.code}"><img src="/${_family.image?:'public/home/home.jpg'}"/></a>
	<a href="/family/${_family.code}">${_family.name}</a>
	#{if _as == 'following'	}<a href="@{Relationz.unfollow(_caller.user.family.code,_family.code)}">&{'unfollow'}</a>#{/if}
</li>