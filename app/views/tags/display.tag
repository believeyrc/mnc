*{ Display a post in one of these modes: 'full', 'home' or 'teaser' }*
 
<div class="post ${_as == 'teaser' ? 'teaser' : ''}">
	#{if _as == 'teaser'}<h6>#{/if}#{else}<h3>#{/else}
        <a href="@{Postz.show(_post.author.fullname,_post.id)}">${_post.title}</a>
	#{if _as == 'teaser'}</h6>#{/if}#{else}</h3>#{/else}

	<div class="quiet">
        <span><a href="@{Postz.index(_post.author.fullname)}"">#{screenname _post.author , href:@Postz.index(_post.author.fullname)/}</a></span> &{'postedAt'}
        <span >${_post.postedAt.format('dd MMM yy')}</span>
        #{if _as == 'teaser'}
		<span class="post-tags">
			- &{'Tagged'}
			#{list items:_post.tags, as:'tag'}
				<a href="@{Postz.listTagged(tag.name,_post.author.fullname)}">${tag}</a>${tag_isLast ? '' : ', '}
			#{/list}
		</span>
		#{/if}
    </div>
	
    #{if _as != 'teaser'}
        <div class="post-content">            
            ${_post.content.nl2br()}
        </div>
    #{/if}
</div>

#{if _as != 'teaser'}
<p class="post-tags">
    - &{'Tagged'}
    #{list items:_post.tags, as:'tag'}
        <a href="@{Postz.listTagged(tag.name,_post.author.fullname)}">${tag}</a>${tag_isLast ? '' : ', '}
    #{/list}
</p>
#{/if}

<br/>