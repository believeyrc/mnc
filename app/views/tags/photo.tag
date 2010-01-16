
#{if _as == '240'}
	<img src='${play.configuration.staticserver}${_photo?.thumb2Path}' alt='${_photo?.caption}'  id="${_id}"/>
#{/if}

#{if _as == '500'}
	<img src='${play.configuration.staticserver}${_photo?.prefPath}' alt='${_photo?.caption}'  id="${_id}"/>
#{/if}

#{if _as == '75'}
	<img src='${play.configuration.staticserver}${_photo?.thumbPath}' alt='${_photo?.caption}' id="${_id}"/>
#{/if}