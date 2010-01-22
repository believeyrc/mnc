(function($) {
    $.fn.collapse = function(setting) {
        return  this.each(function(){
            var o = $.extend({
                 eimg:'/public/images/c.gif',
                 cimg :'/public/images/e.gif'
            },setting);
                var $this = $(this);
                $this.append('<div id="container" style="display:none;"></div>')
                $img = $this.find("img");
                if($img.hasClass("e")){
                    $img.attr("src",o.eimg);
                    var $c = $this.find('#container')
                    $c.show();
                    $c.load($img.attr('rel'))
                } else{
                    $img.attr("src",o.cimg);
                }
            $(this).find("img")['click']( function(){
                var $this = $(this);
                var $c = $this.closest('.collapse').find('#container')
                if($this.attr('src')==o.cimg){
                    $this.attr('src',o.eimg);
                    $c.show();
                    $c.load($this.attr('rel'))
                } else {
                    $this.attr('src',o.cimg);
                    $c.hide();
                }
            })
        });
    }
})(jQuery);

function updatesetsinfo(t,url){
    var dd = $(t).closest('.collapse').find('#container')
    dd.load(url);
}