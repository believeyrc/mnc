#{script "../uploadify/swfobject.js"/}
#{script "../uploadify/jquery.uploadify.v2.1.0.min.js"/}
<script type="text/javascript">
$(document).ready(function() {
	$('#uploadify-div').uploadify({
		'auto': true,
		'scriptData':{"checkuser" : "${request.cookies.PLAY_SESSION?.value?.replaceAll("%00","##")}"},
		'uploader': '/public/uploadify/uploadify.swf',
		'script': '@{Photoz.upload().add('family',_caller.family.code)}',
		multi:true,
		fileDataName: 'upload',
		height:41,
		width:40,
		buttonImg: '/public/uploadify/upload.png',
		'cancelImg': '/public/uploadify/cancel.png'
	});
});
</script>

<style>
.uploadifyQueueItem{
	background-color:#F5F5F5;
	border:2px solid #E5E5E5;
	font:11px Verdana,Geneva,sans-serif;
	margin-top:5px;
	padding:10px;
	width:350px;
}
.uploadifyQueueItem .cancel {
	float:right;	
}
.uploadifyQueueItem .cancel img{
	padding:0px;
}
.uploadifyProgress {
	background-color: #FFFFFF;
	border-top: 1px solid #808080;
	border-left: 1px solid #808080;
	border-right: 1px solid #C5C5C5;
	border-bottom: 1px solid #C5C5C5;
	margin-top: 10px;
	width: 100%;
}
.uploadifyProgressBar {
	background-color: #0099FF;
	width: 1px;
	height: 3px;
}
#uploadtoolbar object{
	float:right;
}
</style>
<div id='uploadtoolbar' >
<div id="uploadify-div">uploadify-div</div>
</div>