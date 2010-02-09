%{
	def count = _items? _items.size() : 0;
	def colc = _cols;
	int lose = count%colc;
	int rowc = count/colc + ( lose > 0? 1 : 0 );
	int rows = _rows ? _rows : 1;
	def fieldsHandler = []
	def errorhandlers = [:];
	_body.setProperty('fieldsHandler', fieldsHandler);
	_body.setProperty('errorhandlers', errorhandlers);

	//out << count << "," << rowc << "," << lose
	if (_items instanceof java.util.Set){
	    _items = new java.util.ArrayList(_items)
	} 
}%

#{doBody as:'skip' /}
<table style="${_style}">
%{
    //out << onnull;
	rowc.times{ r->
		fieldsHandler.each {td->
			out << "<tr>"
			colc.times{ c->
				out << "<td "
				td._attrs.each { a-> 
					out << " " << a.key << "=" << a.value << " "
				}
				out << ">"
				def obj = _items[r*colc+c]
				//out << obj
				if( obj != null ) {				
                    _body.setProperty(_as, obj)
}%				
				    #{doBody body:td._body /}
%{
                } else if(errorhandlers.null !=null ){                      
}%
                    #{doBody body:errorhandlers.null._body/}
%{
                }
			    out << "</td>"
			}
			out << "</tr>"
		}	
	}
}%
</table>
