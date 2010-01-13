<style type="text/css">
#photos{
	border-spacing:2px; 
}
#photos table{
	margin:4px;
	border:0px;	
	padding:1px;
}
#photos td{
	/*text-align:center;
	width: 154px;*/
	padding-left: 2px;
	padding-right: 2px;
	height:22px;
	font-size:15px;
}
#photos input{
	width:230px;
	background:#FFFFD3;
	border-width:1px 0 0 1px;
	border-color:#E9E9AE;
}
#photos textarea{
	width:230px;
	height:75px;
	background:#FFFFD3;
	border-width:1px 0 0 1px;
	border-color:#E9E9AE;
}
.imgtd {
}
.captd {
	font-size:12px;
	width:250px;
	padding-top: 5px;
}

.captd :hover{
	background:#FFFFD3;
}
.infotd{
	font-size:11px;
}

.imgtd #desc{
}
.imgtd #desc :hover{
	background:#FFFFD3;
}

#editor input,#editor textarea ,#editor div{
	margin: 6px 0 6px 0;
}
#editor a,
.infotd a{	
	/*float:right;*/
	background:#0054BD;
	color:#fff;
}
#editor a:hover,
.infotd a:hover{
	background:#0ff;
}
.imgrow{
}
.imgrow img{
	/*width:180px;
	padding:1px;*/
}

tr.caprow{
	font-weight:bold;
	font-size:12px;
	height:18px;
}
tr.inforow{
	color:#212121;
}
</style>

<script>
	function doedit(div,type){
		$(div).hide();
		//类型1 标题，其他就是描述
		if(type ==1){
			$(div).parent().append("<div id='editor'><input id='editor_com' type='input' width='100%' value='"+$(div).text()+"' /> <a onclick='doSave(this,1)'>保存</a>&nbsp;&nbsp;<a onclick='cancelEdit(this,1)' >取消</a></div>");
		}else{
			$(div).parent().append("<div id='editor'><textarea id='editor_com' >"+$(div).text()+"</textarea><a onclick='doSave(this,2)'>保存</a>&nbsp;&nbsp;<a onclick='cancelEdit(this,2)' >取消</a></div>");
		}
	}
	function cancelEdit(bt,t){			
		if(t==1)
			$(bt).closest("div").siblings("#cap").show()
		else
			$(bt).closest("div").siblings("#desc").show()
		$(bt).closest("div").remove();
	}
	function doSave(bt,t){
		var val = $(bt).closest("div").find("#editor_com").val();
		var pre = (t == 1 ? 'cap':'desc');
		var url = (t == 1 ? '@{Photoz.caption()}':'@{Photoz.desc()}');
		divcap = $(bt).closest("div").siblings("#"+pre)
		divcap.show()
		divcap.html('更新中...')
		var callback;
		if(t == 1){
			callback = function(ret){				
				$('[id=cap][pid='+ret.id+']').html(ret.caption)
			};
		} else {
			callback = function(ret){
				$('[id=desc][pid='+ret.id+']').html(ret.description)
			};
		}
		$.post(url, {id:divcap.attr('pid'),val:val},callback, "json");	
		$(bt).closest("div").remove();
	}
	function deletePhoto(a){
		if(confirm('您确定删除么？')){
			var pid = $(a).closest('div').attr('vpid');
			$.post('@{Photoz.remove()}', {id:pid}, function(ret){				
				window.location.reload();
			}, "json");	
		}
		return false;
	}	
</script>


<table id='photos' >
%{
	def imgrow = ""
	def caprow = ""
	def inforow = ""
	def descrow = "";
	def cols = 3
	_photos.eachWithIndex(){photo,i->
		//println(i+":"+photo.id)
		if( i%cols ==0 ){
			imgrow+="<tr valign='top' class='imgrow'>"
			caprow+="<tr valign='top' class='caprow'>"
			//descrow+="<tr valign='top' class='descrow'>"
			//inforow+="<tr valign='top' class='inforow'>"
		}	
		caprow += "<td class='captd'><div id='cap' "
		imgrow += "<td class='imgtd'>"
		imgrow += ( "<div class='imgbock'><a href='"
			+ actionBridge.Photoz.viewPhoto(photo.author.fullname,photo.id)
			+"'><img src='/${photo.thumb2Path}' alt='${photo.caption}'/></a></div>" )
		imgrow += "<div><div id='desc' "
}% 
	#{isMine caller:_caller} %{ 
		caprow += "onclick='doedit(this,1)' " 
		imgrow += "onclick='doedit(this,2)' " 
	}% #{/isMine}
%{ 
		caprow += " pid='${photo.id}'>${photo.caption}</div></td>"
		imgrow += " pid='${photo.id}'>${photo.description?photo.description:'点击这里写修改描述'}</div></div>"
		imgrow += ("<div id='info' vpid='${photo.id}'>"
			+play.i18n.Messages.get('updatedAt')
			+"${photo.uploadAt.format('yyyy/mm/dd')}" )
}%
#{isMine caller:_caller}
	%{ imgrow += ("<a onclick='deletePhoto(this)' >"+play.i18n.Messages.get('deletePhoto')+"</a>")}%
#{/isMine}
%{
			
			imgrow += ( "<a href='"
			+ actionBridge.Photoz.viewPhoto(photo.author.fullname,photo.id)
			+"'> "
			+play.i18n.Messages.get('viewPhoto')
			+"</a>"
			+"</div>")
			imgrow += "</td>" 
		if((i+1) == _photos.size()){
			if (i % cols != cols - 1){
				def lack = cols - ( i % cols ) - 1
				for( j = 0; j < lack; j++) {
					caprow +="<td>&nbsp;</td>"
					imgrow +="<td>&nbsp;</td>"
					//descrow +="<td>&nbsp;</td>"
					//inforow +="<td>&nbsp;</td>"			
				}
			} 
		}
		
		if( i%cols == cols - 1 || (i+1) == _photos.size()){
			out.print(caprow)
			out.print(imgrow)
			//out.print(descrow)
			//out.print(inforow)
			imgrow = ""
			caprow = ""
			descrow = ""
			inforow = ""
		}
	}	
}%
</table>