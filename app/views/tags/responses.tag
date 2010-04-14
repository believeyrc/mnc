    <h4 style='color:#FF0084;'>&{'Responses'}</h4>

#{if _responses.size() >0}
    <table style="border-spacing:0px;">
        #{list items:_responses, as:'resp'}
        <tr><td style='width:48px;padding-right:2px;' class = 'top'>#{avatar user:resp.author/}</td>
        <td style='text-align:left;'>
            <h4 style="margin-bottom:1px;">${resp.author.fullname}&nbsp;说：<small class='float-right'>${resp.postedAt.format('yyyy/MMM/dd')}</small></h4>
            <hr style="margin-bottom:3px;"/>
            <p>${resp.content}</p>
        </td>
        </tr>
        #{/list}
    </table>
#{/if}

#{isLogin}
	#{if _as == 'post'}

	<div style='width:500px'>
    <h4 style='color:#FF0084'>&{'addYourResponse'}</h4>
    #{form @Postz.postComment(_caller.post.id)}
        <p style="margin:15px 5px 5px 5px;">
            <textarea style='width:400px;height:60px' name="content" id="content">${content}</textarea>
        </p>
        <p>
            <input class='button' type="submit" value="&{'SubmitYourComment'}" />
        </p>
    #{/form}
    </div>	
	
	#{/if}
	#{else}
    <div style='width:500px'>
    <h4 style='color:#FF0084'>&{'addYourResponse'}</h4>
    #{form @Photoz.responses(_caller.photo.id)}
        <p style="margin:15px 5px 5px 5px;">
            <textarea style='width:400px;height:60px' name="content" id="content">${content}</textarea>
        </p>
        <p>
            <input class='button' type="submit" value="&{'SubmitYourComment'}" />
        </p>
    #{/form}
    </div>
	#{/else}
#{/isLogin}


#{notLogin}
	<br/>
	已经注册？<a href="@{Secure.login()}">登入</a>吧！
	<br/>
	还没有注册？<a href="@{Userz.register()}">现在加入</a>吧！
	<br/>
	<br/>
#{/notLogin}

