%{
	def count = _items.size()
	def colc = _cols;
	int lose = count%colc;
	int rowc = count/colc + ( lose > 0? 1 : 0 );

	out << count << "," << rowc << "," << lose
}%
<table>
%{
	rowc.times{ r->
		out << "<tr>"
		colc.times{ c->
			out<< "<td>"
			_body.setProperty(_as, _items[r*colc+c])
}%				
			#{doBody/}
%{
			out << "</td>"
		}
		out << "</tr>"
	}	
}%
</table>