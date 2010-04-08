%{
def strOut = play.cache.Cache.get(_name)
if(strOut == null) { 
	strOut = _body.toString() 
	play.cache.Cache.set(_name, strOut)
}
out << strOut
}% 