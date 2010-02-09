%{
java.util.HashMap map = new java.util.HashMap();
map.put("family",  play.mvc.Scope.RenderArgs.current().family?.code);
String url = play.mvc.Router.reverse(_a, map).url;
}%
${url}
