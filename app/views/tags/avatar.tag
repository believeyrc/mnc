<img width="${_as?_as:48}" height="${_as?_as:48}" alt="大头贴" title="到你的所有相片页面"
{%
_attrs.each { a-> 
	out << " " << a.key << "=" << a.value << " "
}
%}
 src="#{if _user.avatar==null}${play.configuration.staticserver}public/images/avatar.jpg#{/if}#{ifnot _user.avatar==null}${play.configuration.staticserver}${_user.avatar}#{/ifnot}">