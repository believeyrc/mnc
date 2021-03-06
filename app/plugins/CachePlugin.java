package plugins;

import java.lang.reflect.Method;

import play.PlayPlugin;
import play.cache.Cache;
import play.mvc.Http.Request;
import play.mvc.results.Result;

public class CachePlugin extends PlayPlugin {

	@Override
	public void onActionInvocationResult(Result result) {
		if (Request.current().invokedMethod != null) {
			Cachable cachable = Request.current().invokedMethod.getAnnotation(Cachable.class);
			if (cachable != null) {
				Cache.add(Request.current().url, result, cachable.value());
			}
		}
	}

	@Override
	public void beforeActionInvocation(Method actionMethod) {
		if (actionMethod != null) {
			Cachable cachable = actionMethod.getAnnotation(Cachable.class);
			if (cachable != null) {
				Result result = (Result) Cache.get(Request.current().url);
				if (result != null)
					throw result;
			}
		}
	}
}
