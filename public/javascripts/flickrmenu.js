
(function($) {
	Flickmenu = function(uo){
		id = "#nicemenu";
		if(uo.id)
			id = uo.id;	
		var o = $.extend({
			 arrawpng:'arraw.png',
			 arrawselectpng :'arrow_select.png',
			 arrawhoverpng : 'arrow_hover.png'
		},uo);
		var img = $(id +" img.arrow");
		img['click'](function(){
			$("span.head_menu").removeClass('active');		
			submenu = $(this).parent().parent().find("div.sub_menu");		
			if(submenu.css('display')=="block"){
				$(this).parent().removeClass("active"); 	
				submenu.hide(); 		
				if(!$(this).hasClass("noswap"))
					$(this).attr('src',o.arrawhoverpng);									
			}else{
				$(this).parent().addClass("active"); 	
				submenu.fadeIn(); 		
				if(!$(this).hasClass('noswap'))
					$(this).attr('src',o.arrawselectpng);	
			}
			
			$("div.sub_menu:visible").not(submenu).hide();			
			$(id +" img[class=arrow]").not(this).attr('src',o.arrawpng);
							
		})
		img['mouseover'](function(){ 
			if(!$(this).hasClass('noswap'))
				$(this).attr('src',o.arrawhoverpng); 
		})
		img['mouseout'](function(){ 
			if($(this).parent().parent().find("div.sub_menu").css('display')!="block"){
				if(!$(this).hasClass('noswap'))
					$(this).attr('src',o.arrawpng);
			}else{
				if(!$(this).hasClass('noswap'))
					$(this).attr('src',o.arrawselectpng);
			}
		});

		$(id +"  span.head_menu")['mouseover'](function(){ 
			$(this).addClass('over')
		})
		.mouseout(function(){ 		
			$(this).removeClass('over') 
		});
		
		subs = $(id +"  div.sub_menu")
		subs['mouseover'](function(){ $(this).fadeIn(); })
	   subs['blur'](function(){ 
			$(this).hide();
			$("span.head_menu").removeClass('active');
		});		
									
		$(document).click(function(event){ 		
				var target = $(event.target);
				if (target.parents(id).length == 0) {				
					$(id +"  span.head_menu").removeClass('active');
					$(id +"  div.sub_menu").hide();					
					$(id +"  img[class=arrow]").attr('src',o.arrawpng);
				}
		});	
	}
	
	$.fn.flickmenu = function(setting) {
            return new Flickmenu(setting);
     };
})(jQuery);