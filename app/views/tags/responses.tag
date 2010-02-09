#{if _responses.size() >0}
    <h4 style='color:#FF0084;font-style:bold;'>&{'Responses'}</h4>
    <table style="border-spacing:8px;">
        #{list items:_responses, as:'resp'}
        <tr><td style='width:48px'>#{avatar user:resp.author/}</td>
        <td style='text-align:left;'>
            <h4>${resp.author.fullname}说：</h4>
            <p>${resp.content}</p>
            <small>${resp.postedAt.format('yyyy/MMM/dd')}</small>
        </td>
        </tr>
        #{/list}
    </table>
#{/if}

#{isLogin}
    <style>
    form{
        border:0px;
        margin:0px;
        padding:0px;
    }
    </style>
    <div style='width:400px'>
    <h4 style='color:#FF0084'>&{'addYourResponse'}</h4>
    #{form @Photoz.responses(_caller.photo.id)}
        <p style="margin:15px 5px 5px 5px;">
            <label for="content">&{'YourMessage'} </label>
            <textarea style='width:400px;height:60px' name="content" id="content">${content}</textarea>
        </p>
        <p>
            <input class='button' type="submit" value="&{'SubmitYourComment'}" />
        </p>
    #{/form}
    </div>
#{/isLogin}
#{notLogin}
	<br/>
	已经注册？<a href="@{Secure.login()}">登入</a>吧！
	<br/>
	还没有注册？<a href="@{Userz.register()}">现在加入</a>吧！
#{/notLogin}