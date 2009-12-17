#{script "../swfupload/swfupload.js"/}
#{script "../swfupload/swfupload.queue.js"/}
#{script "../swfupload/fileprogress.js"/}
#{script "../swfupload/handlers.js"/}
<script>


var msgPending='&{"msgPending"}'
var msgFileTooBig='&{"msgFileTooBig"}'
var msgZeroFile='&{"msgZeroFile"}'
var msgIvalidType='&{"msgIvalidType"}'
var msgFilesCompleted= '&{"msgFilesCompleted"}'
var msgUploading = '&{"msgUploading"}'
var msgComplete='&{"msgComplete"}'
var msgCancelled='&{"msgCancelled"}'

	var swfu;
 function initSwfupload() {
			var settings = {
				flash_url : "@{'/public/swfupload/swfupload.swf'}",
				upload_url: "@{Photoz.upload().add('family',_caller.family.code)}",
				file_post_name: "upload",
				post_params: {"checkuser" : "${request.cookies.PLAY_SESSION?.value.replaceAll("%00","##")}"},
				file_size_limit : "2 MB",
				file_types : "*.jpg;*.jpeg;*.png;*.gif",
				file_types_description : "Image Files",
				file_upload_limit : 100,
				file_queue_limit : 0,
				custom_settings : {
					progressTarget : "fsUploadProgress",
					cancelButtonId : "btnCancel"
				},
				debug: false,

				// Button settings
				button_image_url: "@{'/public/swfupload/TestImageNoText_65x29.png'}",
				button_width: "65",
				button_height: "29",
				button_placeholder_id: "spanButtonPlaceHolder",
				button_text: '<span class="theFont">&{'selectFileToUpload'}</span>',
				button_text_style: ".theFont { font-size: 12; }",
				button_text_left_padding: 4,
				button_text_top_padding: 3,
				
				// The event handler functions are defined in handlers.js
				file_queued_handler : fileQueued,
				file_queue_error_handler : fileQueueError,
				file_dialog_complete_handler : fileDialogComplete,
				upload_start_handler : uploadStart,
				upload_progress_handler : uploadProgress,
				upload_error_handler : uploadError,
				upload_success_handler : uploadSuccess,
				upload_complete_handler : uploadComplete,
				queue_complete_handler : queueComplete	// Queue plugin event
			};

			swfu = new SWFUpload(settings);
	     };
	     
	     $(document).ready(initSwfupload);
</script>
<form id="form1" method="post" enctype="multipart/form-data">
		<div class="fieldset flash" id="fsUploadProgress">
			<span class="legend"></span>
		</div>
		<div id="divStatus"></div>
		<div style="padding:0px">
			<table style="margin:1px"><tr><td><span id="spanButtonPlaceHolder"></span></td>
			<td><input id="btnCancel" type="button" value="&{'uploadCancel'}" onclick="swfu.cancelQueue();" disabled="disabled"/></td>
			</tr></table>
		</div>
</form>
