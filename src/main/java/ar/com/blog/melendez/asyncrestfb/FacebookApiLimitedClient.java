package ar.com.blog.melendez.asyncrestfb;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import akka.actor.ActorRef;
import akka.dispatch.Await;
import akka.dispatch.Future;
import akka.pattern.Patterns;
import akka.util.Duration;
import akka.util.Timeout;

import com.restfb.WebRequestor;

public class FacebookApiLimitedClient implements InvocationHandler {

	private final WebRequestor proxied;
	private final ActorRef cordinator;

	public FacebookApiLimitedClient(WebRequestor proxied, ActorRef actorRef) {
		this.proxied = proxied;
		this.cordinator = actorRef;
	}

	@Override
	public Object invoke(Object arg1, Method method, Object[] args)
			throws Throwable {

		Timeout timeout = new Timeout(Duration.parse("5000 seconds"));
		Future<Object> future = Patterns.ask(cordinator, new Cordinate(),
				timeout);
		String result = (String) Await.result(future, timeout.duration());
		try {
			return method.invoke(proxied, args);
		} catch (com.restfb.exception.FacebookOAuthException fbException) {
			fbException.printStackTrace();
			return null;
		}
	}

}
