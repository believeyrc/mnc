%{
	def count = _items.size()
	def colc = _cols;
	int lose = count%colc;
	int rowc = count/colc + ( lose > 0? 1 : 0 );
	int rows = _rows ? _rows : 1;
	def fieldsHandler = []	
	
	_body.setProperty('fieldsHandler', fieldsHandler);
	//out << count << "," << rowc << "," << lose
	
}%
#{doBody as:'skip' /}
<table>
%{
	rowc.times{ r->
		fieldsHandler.each {td->
			out << "<tr>"
			
			colc.times{ c->
				out<< "<td "
				td._attrs.each{a-> 
					out << " " << a.key << "=" << a.value << " "
				}
				out<< ">"
				_body.setProperty(_as, _items[r*colc+c])
}%				
				#{doBody body:td._body /}
%{
				out << "</td>"
			}
			out << "</tr>"
		}	
	}
}%
</table>
