<ul id="pagination">
#{if _caller.offset > 0}
        <li id="previous">
			<a href="@{history().add('offset',(_caller.offset-_caller.pageSize) > 0 ? (_caller.offset-_caller.pageSize) : 0).add('pageSize', _caller.pageSize)}">&{'btNewerPosts'}</a>
        </li>
    #{/if}
#{if _caller.offset + _caller.pageSize < _caller.totalCount }
	<li id="next">
		<a href="@{history().add('offset',_caller.offset + _caller.pageSize).add('pageSize', _caller.pageSize)}">&{'btOlderPosts'}</a>
    </li>
#{/if}
</ul>