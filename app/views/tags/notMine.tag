#{if _caller.user == null || _caller.user.fullname != _caller.currentuser }
    #{doBody/}
#{/if}
