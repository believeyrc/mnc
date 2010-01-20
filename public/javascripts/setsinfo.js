    function updatesetsinfo(t,url){
        var dd = $(t).closest('.collapse').find('#container')
        dd.fadeOut("normal").load(url).fadeIn('normal');
    }
