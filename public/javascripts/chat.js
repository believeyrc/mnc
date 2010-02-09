/* 
Created by: Kenrick Beckett

Name: Chat Engine
*/

var instanse = false;
var state = 0;
var mes;
var file;
var updateurl;
var stateOfChatUrl;
var sendMsgUrl;
function Chat () {
    this.update = updateChat;
    this.send = sendChat;
	this.loadHistory = loadHistory;
}
function loadHistory(){
	if(!instanse){
		instanse = true;
		var fstate = $("#chat-area").children(':first').attr('state');
		var ca = document.getElementById('chat-area')
		var oldpos = ca.scrollHeight
		$.ajax({
		   type: "POST",
		   url: updateurl,
		   data: {  
					'id': channelid,
					'state': fstate,
					direct : -1
				},
		   dataType: "json",
		   success: function(data){
			   if(data.messages){
					for (var i = 0; i < data.messages.length; i++) {
						var msg = data.messages[i];
						$('#chat-area').prepend(packToHtml(msg));
					}								  
			   }			   
			   //document.getElementById('chat-area').scrollTop = document.getElementById('chat-area').scrollHeight;
			   ca.scrollTop = ca.scrollHeight-oldpos;
			   instanse = false;
		   },
		});			
	}	
}
function packToHtml(msg){
	return $("<p state=" +msg.longTime+ ">"
						+"<em>"+ msg.updatedDate +"</em>"
						+"<span>"+ msg.nickname +"</span>"
						+msg.content +"</p>")
}
//Updates the chat
function updateChat(){
	if(!instanse){
		instanse = true;
		var ca = document.getElementById('chat-area');
		var autoscroll = ca.scrollHeight - ca.clientHeight - ca.scrollTop  < 10;
		$.ajax({
		   type: "POST",
		   url: updateurl,
		   data: {  
					'id': channelid,
					'state': state,
					direct : 1
				},
		   dataType: "json",
		   success: function(data){
			   if(data.messages){
					for (var i = 0; i < data.messages.length; i++) {
						var msg = data.messages[i];
						$('#chat-area').append(packToHtml(msg));
					}								  
			   }
			   //alert(document.getElementById('chat-area').scrollTop);
			   
			   if(autoscroll)
					ca.scrollTop = ca.scrollHeight;
			   instanse = false;
			   state = data.state;
		   },
		});			
	}
	//setTimeout(updateChat, 1500);
}

//send the message
function sendChat(message, nickname){   
     $.ajax({
		   type: "POST",
		   url: sendMsgUrl,
		   data: {  'id': channelid,
					'content': message,
					'nickname': nickname
				 },
		   dataType: "json",
		   success: function(data){
			   updateChat();
		   },
		});
}
