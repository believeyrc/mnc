*{ Display a post in one of these modes: 'full', 'home' or 'teaser' }*
 
<div class="post ${_as == 'teaser' ? 'teaser' : ''}">
    <h3 class="post-title">
        <a href="@{Postz.show(_post.author.fullname,_post.id)}">${_post.title}</a>
    </h3>
    <div class="post-metadata">
        <span class="post-author"> <a href="@{Postz.index(_post.author.fullname)}"">#{screenname _post.author , href:@Postz.index(_post.author.fullname)/}</a></span> &{'postedAt'}
        <span class="post-date">${_post.postedAt.format('dd MMM yy')}</span>
        #{if _as != 'full'}
            <span class="post-comments">
                &nbsp;|&nbsp; ${_post.comments.size() ?: 'no'} 
                comment${_post.comments.size().pluralize()}
                #{if _post.comments}
                    , latest by ${_post.comments[0].author}
                #{/if}
            </span>
        #{/if}
    </div>
    #{if _as != 'teaser'}
        <div class="post-content">            
            ${_post.content.nl2br()}
        </div>
    #{/if}
</div>
 
#{if _as == 'full'}
    <div class="comments">
        <h3>
            ${_post.comments.size() ?: 'no'} 
            &{'Comments'}
        </h3>
        
        #{list items:_post.comments, as:'comment'}
            <div class="comment">                
				<div class="comment-metadata">
                    <span class="comment-date">${comment.postedAt.format('yyyy/MM/dd')}</span>
                    <span class="comment-author">${comment.author}:</span>
                </div>
                <div class="comment-content">                    
                    ${comment.content.escape().nl2br()}
                </div>
            </div>
        #{/list}
        
    </div>
#{/if}
*{elseif _post.tags}*
    <span class="post-tags">
        - &{'Tagged'}
        #{list items:_post.tags, as:'tag'}
            <a href="@{Postz.listTagged(tag.name,_post.author.fullname)}">${tag}</a>${tag_isLast ? '' : ', '}
        #{/list}
    </span>
*{/elseif}*
