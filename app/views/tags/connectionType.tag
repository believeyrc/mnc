#{if _as == 'checkbox'}
	<input disabled='true' type="checkbox" #{if (_arg.type & 1) == 1} checked #{/if} /><span class="">自己人</span>
	<input disabled='true' type="checkbox" #{if (_arg.type & 2) == 2} checked #{/if} /><span class="">朋友</span>
	<input disabled='true' type="checkbox" #{if (_arg.type & 4) == 4} checked #{/if} /><span class="">家人</span>
#{/if}
#{else}
	#{if (_arg.type & 1) == 1} <span class="added">自己人</span> #{/if} 
	#{if (_arg.type & 2) == 2} <span class="added">朋友</span> #{/if} 
	#{if (_arg.type & 4) == 4} <span class="added">家人</span> #{/if}
#{/else}